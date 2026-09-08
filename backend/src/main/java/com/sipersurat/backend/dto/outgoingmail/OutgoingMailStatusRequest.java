package com.sipersurat.backend.dto.outgoingmail;

import com.sipersurat.backend.enums.OutgoingMailStatus;
import jakarta.validation.constraints.NotNull;

public record OutgoingMailStatusRequest(
        @NotNull(message="Status surat wajib dipilih")
        OutgoingMailStatus status
){}