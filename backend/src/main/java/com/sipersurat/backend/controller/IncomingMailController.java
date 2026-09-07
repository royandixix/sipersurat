package com.sipersurat.backend.controller;

import com.sipersurat.backend.dto.incomingmail.IncomingMailRequest;
import com.sipersurat.backend.dto.incomingmail.IncomingMailResponse;
import com.sipersurat.backend.dto.incomingmail.IncomingMailStatusRequest;
import com.sipersurat.backend.response.ApiResponse;
import com.sipersurat.backend.service.IncomingMailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incoming-mails")
public class IncomingMailController {

    private final IncomingMailService service;

    public IncomingMailController(
            IncomingMailService service
    ) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<IncomingMailResponse>>
    getAll() {

        return ApiResponse.ok(
                "Data surat masuk berhasil dimuat",
                service.getAll()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<IncomingMailResponse>
    getById(
            @PathVariable Long id
    ) {

        return ApiResponse.ok(
                "Detail surat masuk berhasil dimuat",
                service.getById(id)
        );
    }

    @PostMapping
    public ResponseEntity<
            ApiResponse<IncomingMailResponse>
            > create(
            @Valid @RequestBody
            IncomingMailRequest request
    ) {

        IncomingMailResponse result =
                service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.ok(
                                "Surat masuk berhasil ditambahkan",
                                result
                        )
                );
    }

    @PutMapping("/{id}")
    public ApiResponse<IncomingMailResponse>
    update(
            @PathVariable Long id,

            @Valid @RequestBody
            IncomingMailRequest request
    ) {

        return ApiResponse.ok(
                "Surat masuk berhasil diperbarui",
                service.update(id, request)
        );
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<IncomingMailResponse>
    updateStatus(
            @PathVariable Long id,

            @Valid @RequestBody
            IncomingMailStatusRequest request
    ) {

        return ApiResponse.ok(
                "Status surat berhasil diperbarui",
                service.updateStatus(
                        id,
                        request.status()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void>
    delete(
            @PathVariable Long id
    ) {

        service.delete(id);

        return ApiResponse.ok(
                "Surat masuk berhasil dihapus",
                null
        );
    }
}