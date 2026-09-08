package com.sipersurat.backend.dto.outgoingmail;

import com.sipersurat.backend.enums.OutgoingMailStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record OutgoingMailRequest(
        @NotBlank(message="Nomor surat wajib diisi")
        String letterNumber,

        @NotNull(message="Tanggal surat wajib diisi")
        LocalDate letterDate,

        @NotBlank(message="Tujuan surat wajib diisi")
        String recipient,

        @NotBlank(message="Perihal wajib diisi")
        String subject,

        @NotBlank(message="Kategori surat wajib dipilih")
        String category,

        @NotBlank(message="Sifat surat wajib dipilih")
        String priority,

        @NotBlank(message="Unit pembuat wajib dipilih")
        String sourceUnit,

        @NotBlank(message="Penandatangan wajib diisi")
        String signer,

        String notes,
        OutgoingMailStatus status,
        LocalDate sentDate,
        String fileName,
        String fileType,
        Long fileSize
){}