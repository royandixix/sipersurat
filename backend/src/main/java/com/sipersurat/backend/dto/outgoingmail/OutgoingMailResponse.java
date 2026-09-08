package com.sipersurat.backend.dto.outgoingmail;

import com.sipersurat.backend.enums.OutgoingMailStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record OutgoingMailResponse(
        Long id,
        String agendaNumber,
        String letterNumber,
        LocalDate letterDate,
        String recipient,
        String subject,
        String category,
        String priority,
        String sourceUnit,
        String signer,
        String notes,
        OutgoingMailStatus status,
        LocalDate sentDate,
        String fileName,
        String fileType,
        Long fileSize,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
){}