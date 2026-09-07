package com.sipersurat.backend.dto.incomingmail;

import com.sipersurat.backend.enums.IncomingMailStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record IncomingMailRequest(

        @NotBlank(message = "Nomor surat wajib diisi")
        String letterNumber,

        @NotNull(message = "Tanggal surat wajib diisi")
        LocalDate letterDate,

        @NotNull(message = "Tanggal diterima wajib diisi")
        LocalDate receivedDate,

        @NotBlank(message = "Pengirim wajib diisi")
        String sender,

        @NotBlank(message = "Perihal wajib diisi")
        String subject,

        String category,

        String priority,

        String targetUnit,

        String notes,

        IncomingMailStatus status,

        String fileName,

        String fileType,

        Long fileSize

) {
}