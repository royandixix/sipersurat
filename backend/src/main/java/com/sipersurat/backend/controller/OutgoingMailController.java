package com.sipersurat.backend.controller;

import com.sipersurat.backend.dto.outgoingmail.OutgoingMailRequest;
import com.sipersurat.backend.dto.outgoingmail.OutgoingMailResponse;
import com.sipersurat.backend.dto.outgoingmail.OutgoingMailStatusRequest;
import com.sipersurat.backend.response.ApiResponse;
import com.sipersurat.backend.service.OutgoingMailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/outgoing-mails")
public class OutgoingMailController{
    private final OutgoingMailService service;

    public OutgoingMailController(OutgoingMailService service){
        this.service=service;
    }

    @GetMapping
    public ApiResponse<List<OutgoingMailResponse>> getAll(){
        return ApiResponse.ok(
                "Data surat keluar berhasil dimuat",
                service.getAll()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<OutgoingMailResponse> getById(@PathVariable Long id){
        return ApiResponse.ok(
                "Detail surat keluar berhasil dimuat",
                service.getById(id)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OutgoingMailResponse>> create(
            @Valid @RequestBody OutgoingMailRequest request
    ){
        OutgoingMailResponse result=service.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.ok(
                                "Surat keluar berhasil ditambahkan",
                                result
                        )
                );
    }

    @PutMapping("/{id}")
    public ApiResponse<OutgoingMailResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody OutgoingMailRequest request
    ){
        return ApiResponse.ok(
                "Surat keluar berhasil diperbarui",
                service.update(id,request)
        );
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<OutgoingMailResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OutgoingMailStatusRequest request
    ){
        return ApiResponse.ok(
                "Status surat keluar berhasil diperbarui",
                service.updateStatus(id,request.status())
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ApiResponse.ok(
                "Surat keluar berhasil dihapus",
                null
        );
    }
}