package com.sipersurat.backend.service;

import com.sipersurat.backend.dto.masterdata.MasterDataRequest;
import com.sipersurat.backend.dto.masterdata.MasterDataResponse;
import com.sipersurat.backend.entity.MasterData;
import com.sipersurat.backend.enums.MasterDataStatus;
import com.sipersurat.backend.enums.MasterDataType;
import com.sipersurat.backend.enums.UserRole;
import com.sipersurat.backend.exception.BadRequestException;
import com.sipersurat.backend.exception.DuplicateResourceException;
import com.sipersurat.backend.exception.ResourceNotFoundException;
import com.sipersurat.backend.repository.IncomingMailRepository;
import com.sipersurat.backend.repository.MasterDataRepository;
import com.sipersurat.backend.repository.OutgoingMailRepository;
import com.sipersurat.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;

@Service
public class MasterDataService {
    private final MasterDataRepository repository;
    private final UserRepository userRepository;
    private final IncomingMailRepository incomingMailRepository;
    private final OutgoingMailRepository outgoingMailRepository;

    public MasterDataService(
            MasterDataRepository repository,
            UserRepository userRepository,
            IncomingMailRepository incomingMailRepository,
            OutgoingMailRepository outgoingMailRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.incomingMailRepository = incomingMailRepository;
        this.outgoingMailRepository = outgoingMailRepository;
    }

    @Transactional(readOnly = true)
    public List<MasterDataResponse> getAll() {
        return repository.findAllByOrderByTypeAscNameAsc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MasterDataResponse getById(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public MasterDataResponse create(MasterDataType type, MasterDataRequest request) {
        String code = normalizeCode(request.code());
        if (repository.existsByTypeAndCodeIgnoreCase(type, code)) {
            throw new DuplicateResourceException("Kode " + code + " sudah digunakan");
        }
        MasterData item = new MasterData();
        item.setType(type);
        apply(item, request, code);
        return toResponse(repository.save(item));
    }

    @Transactional
    public MasterDataResponse update(Long id, MasterDataRequest request) {
        MasterData item = find(id);
        String code = normalizeCode(request.code());
        if (repository.existsByTypeAndCodeIgnoreCaseAndIdNot(item.getType(), code, id)) {
            throw new DuplicateResourceException("Kode " + code + " sudah digunakan");
        }
        if (item.getType() == MasterDataType.ROLE && "SUPER_ADMIN".equals(item.getCode()) && !"SUPER_ADMIN".equals(code)) {
            throw new BadRequestException("Kode Super Administrator tidak dapat diubah");
        }
        apply(item, request, code);
        return toResponse(repository.save(item));
    }

    @Transactional
    public MasterDataResponse toggleStatus(Long id) {
        MasterData item = find(id);
        if (item.getType() == MasterDataType.ROLE && "SUPER_ADMIN".equals(item.getCode())) {
            throw new BadRequestException("Super Administrator tidak dapat dinonaktifkan");
        }
        item.setStatus(item.getStatus() == MasterDataStatus.ACTIVE ? MasterDataStatus.INACTIVE : MasterDataStatus.ACTIVE);
        return toResponse(repository.save(item));
    }

    @Transactional
    public void delete(Long id) {
        MasterData item = find(id);
        if (item.getType() == MasterDataType.ROLE && "SUPER_ADMIN".equals(item.getCode())) {
            throw new BadRequestException("Super Administrator tidak dapat dihapus");
        }
        long usage = usageCount(item);
        if (usage > 0) {
            throw new BadRequestException(item.getName() + " masih digunakan dan belum dapat dihapus");
        }
        repository.delete(item);
    }

    private void apply(MasterData item, MasterDataRequest request, String code) {
        item.setName(request.name().trim());
        item.setCode(code);
        item.setDescription(request.description() == null ? "" : request.description().trim());
        item.setStatus(request.status());
    }

    private String normalizeCode(String value) {
        if (value == null || value.isBlank()) throw new BadRequestException("Kode wajib diisi");
        return value.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_").replaceAll("^_+|_+$", "");
    }

    private MasterData find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Master data tidak ditemukan"));
    }

    private MasterDataResponse toResponse(MasterData item) {
        return new MasterDataResponse(
                item.getId(), item.getType(), item.getName(), item.getCode(), item.getDescription(),
                item.getStatus(), usageCount(item), item.getCreatedAt(), item.getUpdatedAt());
    }

    private long usageCount(MasterData item) {
        String name = item.getName();
        return switch (item.getType()) {
            case ROLE -> roleUsage(item.getCode());
            case UNIT_KERJA -> userRepository.countByDepartmentIgnoreCase(name)
                    + incomingMailRepository.countByTargetUnitIgnoreCase(name)
                    + outgoingMailRepository.countBySourceUnitIgnoreCase(name);
            case KATEGORI_SURAT -> incomingMailRepository.countByCategoryIgnoreCase(name)
                    + outgoingMailRepository.countByCategoryIgnoreCase(name);
            case SIFAT_SURAT -> incomingMailRepository.countByPriorityIgnoreCase(name)
                    + outgoingMailRepository.countByPriorityIgnoreCase(name);
        };
    }

    private long roleUsage(String code) {
        try {
            return userRepository.countByRole(UserRole.valueOf(code));
        } catch (IllegalArgumentException ex) {
            return 0;
        }
    }
}
