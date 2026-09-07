package com.sipersurat.backend.controller;

import com.sipersurat.backend.dto.user.ResetPasswordRequest;
import com.sipersurat.backend.dto.user.UpdateUserStatusRequest;
import com.sipersurat.backend.dto.user.UserRequest;
import com.sipersurat.backend.dto.user.UserResponse;
import com.sipersurat.backend.dto.user.UserSummaryResponse;
import com.sipersurat.backend.enums.UserRole;
import com.sipersurat.backend.enums.UserStatus;
import com.sipersurat.backend.response.ApiResponse;
import com.sipersurat.backend.response.PageResponse;
import com.sipersurat.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        return ApiResponse.ok(
                "Data pengguna berhasil dimuat",
                service.getUsers(search, role, status, page, size)
        );
    }

    @GetMapping("/summary")
    public ApiResponse<UserSummaryResponse> summary() {
        return ApiResponse.ok(
                "Ringkasan pengguna berhasil dimuat",
                service.getSummary()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
        return ApiResponse.ok(
                "Pengguna berhasil dimuat",
                service.getUser(id)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(
            @Valid @RequestBody UserRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.ok(
                                "Pengguna berhasil ditambahkan",
                                service.create(request)
                        )
                );
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {

        return ApiResponse.ok(
                "Pengguna berhasil diperbarui",
                service.update(id, request)
        );
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UserResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        return ApiResponse.ok(
                "Status pengguna berhasil diperbarui",
                service.updateStatus(id, request)
        );
    }

    @PatchMapping("/{id}/reset-password")
    public ApiResponse<Void> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordRequest request) {

        service.resetPassword(id, request);

        return ApiResponse.ok(
                "Password berhasil diperbarui",
                null
        );
    }

    @PatchMapping("/{id}/last-login")
    public ApiResponse<Void> updateLastLogin(@PathVariable Long id) {

        service.updateLastLogin(id);

        return ApiResponse.ok(
                "Waktu login terakhir berhasil diperbarui",
                null
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ApiResponse.ok(
                "Pengguna berhasil dihapus",
                null
        );
    }
}