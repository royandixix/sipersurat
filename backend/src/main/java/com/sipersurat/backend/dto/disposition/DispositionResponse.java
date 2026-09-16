package com.sipersurat.backend.dto.disposition;

import com.sipersurat.backend.enums.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record DispositionResponse(Long id, String code, Long incomingMailId, String agendaNumber, String letterNumber,
        String sender, String subject, DispositionTargetType targetType, String targetId, String targetName,
        String instruction, DispositionPriority priority, LocalDate dueDate, String notes, DispositionStatus status,
        String createdBy, OffsetDateTime createdAt, OffsetDateTime updatedAt,
        List<DispositionHistoryResponse> history) {
}
