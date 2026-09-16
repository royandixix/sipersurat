package com.sipersurat.backend.controller;

import com.sipersurat.backend.dto.disposition.DispositionRequest;
import com.sipersurat.backend.dto.disposition.DispositionResponse;
import com.sipersurat.backend.dto.disposition.DispositionStatusRequest;
import com.sipersurat.backend.response.ApiResponse;
import com.sipersurat.backend.service.DispositionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dispositions")
public class DispositionController {
    private final DispositionService service;
    public DispositionController(DispositionService service) { this.service = service; }

    @GetMapping
    public ApiResponse<List<DispositionResponse>> getAll() {
        return ApiResponse.ok("Data disposisi berhasil dimuat", service.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<DispositionResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok("Detail disposisi berhasil dimuat", service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DispositionResponse>> create(@Valid @RequestBody DispositionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Disposisi berhasil dibuat", service.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<DispositionResponse> update(@PathVariable Long id, @Valid @RequestBody DispositionRequest request) {
        return ApiResponse.ok("Disposisi berhasil diperbarui", service.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<DispositionResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody DispositionStatusRequest request) {
        return ApiResponse.ok("Status disposisi berhasil diperbarui", service.updateStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok("Disposisi berhasil dihapus", null);
    }
}
