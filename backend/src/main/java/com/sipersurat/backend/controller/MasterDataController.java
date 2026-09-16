package com.sipersurat.backend.controller;

import com.sipersurat.backend.dto.masterdata.MasterDataRequest;
import com.sipersurat.backend.dto.masterdata.MasterDataResponse;
import com.sipersurat.backend.enums.MasterDataType;
import com.sipersurat.backend.response.ApiResponse;
import com.sipersurat.backend.service.MasterDataService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/master-data")
public class MasterDataController {
    private final MasterDataService service;
    public MasterDataController(MasterDataService service) { this.service = service; }

    @GetMapping
    public ApiResponse<List<MasterDataResponse>> getAll() {
        return ApiResponse.ok("Master data berhasil dimuat", service.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<MasterDataResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok("Master data berhasil dimuat", service.getById(id));
    }

    @PostMapping("/{type}")
    public ResponseEntity<ApiResponse<MasterDataResponse>> create(@PathVariable MasterDataType type, @Valid @RequestBody MasterDataRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Master data berhasil ditambahkan", service.create(type, request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<MasterDataResponse> update(@PathVariable Long id, @Valid @RequestBody MasterDataRequest request) {
        return ApiResponse.ok("Master data berhasil diperbarui", service.update(id, request));
    }

    @PatchMapping("/{id}/toggle-status")
    public ApiResponse<MasterDataResponse> toggleStatus(@PathVariable Long id) {
        return ApiResponse.ok("Status master data berhasil diperbarui", service.toggleStatus(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok("Master data berhasil dihapus", null);
    }
}
