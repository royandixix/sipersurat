package com.sipersurat.backend.controller;

import tools.jackson.databind.JsonNode;
import com.sipersurat.backend.dto.settings.SettingsRequest;
import com.sipersurat.backend.response.ApiResponse;
import com.sipersurat.backend.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settings")
public class SettingsController {
    private final SettingsService service;
    public SettingsController(SettingsService service) { this.service = service; }

    @GetMapping
    public ApiResponse<JsonNode> getSettings() {
        return ApiResponse.ok("Pengaturan berhasil dimuat", service.getSettings());
    }

    @PutMapping
    public ApiResponse<JsonNode> saveSettings(@Valid @RequestBody SettingsRequest request) {
        return ApiResponse.ok("Pengaturan berhasil disimpan", service.saveSettings(request.settings()));
    }

    @PostMapping("/reset")
    public ApiResponse<JsonNode> resetSettings() {
        return ApiResponse.ok("Pengaturan berhasil dipulihkan", service.resetSettings());
    }

    @GetMapping("/backup")
    public ApiResponse<JsonNode> backup() {
        return ApiResponse.ok("Backup berhasil dibuat", service.createBackup());
    }

    @PostMapping("/restore")
    public ApiResponse<JsonNode> restore(@RequestBody JsonNode backup) {
        return ApiResponse.ok("Backup berhasil dipulihkan", service.restoreBackup(backup));
    }

    @DeleteMapping("/operational-data")
    public ApiResponse<Void> resetOperationalData() {
        service.resetOperationalData();
        return ApiResponse.ok("Data operasional berhasil direset", null);
    }
}

