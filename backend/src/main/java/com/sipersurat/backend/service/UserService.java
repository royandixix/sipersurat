package com.sipersurat.backend.service;

import java.time.OffsetDateTime;
import java.util.Locale;
import com.sipersurat.backend.dto.user.ResetPasswordRequest;
import com.sipersurat.backend.dto.user.UpdateUserStatusRequest;
import com.sipersurat.backend.dto.user.UserRequest;
import com.sipersurat.backend.dto.user.UserResponse;
import com.sipersurat.backend.dto.user.UserSummaryResponse;
import com.sipersurat.backend.entity.User;
import com.sipersurat.backend.enums.UserRole;
import com.sipersurat.backend.enums.UserStatus;
import com.sipersurat.backend.exception.BadRequestException;
import com.sipersurat.backend.exception.DuplicateResourceException;
import com.sipersurat.backend.exception.ResourceNotFoundException;
import com.sipersurat.backend.repository.UserRepository;
import com.sipersurat.backend.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, AuditLogService auditLogService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsers(String search, UserRole role, UserStatus status, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        PageRequest pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> result = repository.search(normalizeSearch(search), role, status, pageable);
        return new PageResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast());
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return toResponse(findUser(id));
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse getSummary() {
        return new UserSummaryResponse(
                repository.count(),
                repository.countByStatus(UserStatus.ACTIVE),
                repository.countByStatus(UserStatus.INACTIVE),
                repository.countByRole(UserRole.SUPER_ADMIN));
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        String email = normalizeEmail(request.email());
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email tersebut sudah digunakan");
        }
        validatePassword(request.password(), true);
        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setDepartment(request.department().trim());
        user.setStatus(request.status());
        User saved = repository.save(user);
        audit("CREATE_USER", "Menambahkan pengguna", saved.getEmail());
        return toResponse(saved);
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = findUser(id);
        String email = normalizeEmail(request.email());
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateResourceException("Email tersebut sudah digunakan");
        }
        protectLastSuperAdminOnUpdate(user, request);
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setRole(request.role());
        user.setDepartment(request.department().trim());
        user.setStatus(request.status());
        if (request.password() != null && !request.password().isBlank()) {
            validatePassword(request.password(), false);
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        User saved = repository.save(user);
        audit("UPDATE_USER", "Memperbarui pengguna", saved.getEmail());
        return toResponse(saved);
    }

    @Transactional
    public UserResponse updateStatus(Long id, UpdateUserStatusRequest request) {
        User user = findUser(id);
        if (request.status() == null) {
            throw new BadRequestException("Status pengguna wajib diisi");
        }
        if (user.getRole() == UserRole.SUPER_ADMIN
                && user.getStatus() == UserStatus.ACTIVE
                && request.status() == UserStatus.INACTIVE
                && repository.countByRoleAndStatus(UserRole.SUPER_ADMIN, UserStatus.ACTIVE) <= 1) {
            throw new BadRequestException("Minimal satu Super Administrator aktif harus tersedia");
        }
        user.setStatus(request.status());
        User saved = repository.save(user);
        audit(
                request.status() == UserStatus.ACTIVE ? "ACTIVATE_USER" : "DEACTIVATE_USER",
                request.status() == UserStatus.ACTIVE ? "Mengaktifkan pengguna" : "Menonaktifkan pengguna",
                saved.getEmail());
        return toResponse(saved);
    }

    @Transactional
    public void resetPassword(Long id, ResetPasswordRequest request) {
        User user = findUser(id);
        validatePassword(request.newPassword(), true);
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        repository.save(user);
        audit("RESET_PASSWORD", "Mereset password pengguna", user.getEmail());
    }

    @Transactional
    public void updateLastLogin(Long id) {
        User user = findUser(id);
        user.setLastLoginAt(OffsetDateTime.now());
        repository.save(user);
        audit("UPDATE_LAST_LOGIN", "Memperbarui waktu login terakhir", user.getEmail());
    }

    @Transactional
    public void delete(Long id) {
        User user = findUser(id);
        if (user.getRole() == UserRole.SUPER_ADMIN) {
            long totalSuperAdmin = repository.countByRole(UserRole.SUPER_ADMIN);
            if (totalSuperAdmin <= 1) {
                throw new BadRequestException("Super Administrator terakhir tidak dapat dihapus");
            }
            long activeSuperAdmin = repository.countByRoleAndStatus(
                    UserRole.SUPER_ADMIN,
                    UserStatus.ACTIVE);
            if (user.getStatus() == UserStatus.ACTIVE && activeSuperAdmin <= 1) {
                throw new BadRequestException("Super Administrator aktif terakhir tidak dapat dihapus");
            }
        }
        String email = user.getEmail();
        String name = user.getName();
        repository.delete(user);
        audit("DELETE_USER", "Menghapus pengguna " + name, email);
    }

    private User findUser(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan"));
    }

    private void protectLastSuperAdminOnUpdate(User user, UserRequest request) {
        if (user.getRole() != UserRole.SUPER_ADMIN) {
            return;
        }
        boolean changingRole = request.role() != UserRole.SUPER_ADMIN;
        boolean becomingInactive = request.status() != UserStatus.ACTIVE;
        long totalSuperAdmin = repository.countByRole(UserRole.SUPER_ADMIN);
        if (changingRole && totalSuperAdmin <= 1) {
            throw new BadRequestException("Super Administrator terakhir tidak dapat diubah ke role lain");
        }
        boolean removingActiveSuperAdmin = user.getStatus() == UserStatus.ACTIVE
                && (changingRole || becomingInactive);
        if (removingActiveSuperAdmin) {
            long activeSuperAdmin = repository.countByRoleAndStatus(
                    UserRole.SUPER_ADMIN,
                    UserStatus.ACTIVE);
            if (activeSuperAdmin <= 1) {
                throw new BadRequestException("Minimal satu Super Administrator aktif harus tersedia");
            }
        }
    }

    private void validatePassword(String password, boolean required) {
        if (required && (password == null || password.isBlank())) {
            throw new BadRequestException("Password wajib diisi");
        }
        if (password != null && !password.isBlank() && password.length() < 8) {
            throw new BadRequestException("Password minimal 8 karakter");
        }
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email wajib diisi");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeSearch(String search) {
        if (search == null) {
            return null;
        }
        String value = search.trim();
        return value.isEmpty() ? null : value;
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getDepartment(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginAt());
    }

    private void audit(String action, String description, String value) {
        auditLogService.record(
                "Super Administrator",
                "SA",
                action,
                description,
                value);
    }
}