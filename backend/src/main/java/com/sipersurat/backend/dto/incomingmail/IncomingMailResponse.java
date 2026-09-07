package com.sipersurat.backend.dto.incomingmail;

import com.sipersurat.backend.enums.IncomingMailStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record IncomingMailResponse(

        Long id,

        String agendaNumber,

        String letterNumber,

        LocalDate letterDate,

        LocalDate receivedDate,

        String sender,

        String subject,

        String category,

        String priority,

        String targetUnit,

        String notes,

        IncomingMailStatus status,

        String fileName,

        String fileType,

        Long fileSize,

        OffsetDateTime createdAt,

        OffsetDateTime updatedAt

) {
}