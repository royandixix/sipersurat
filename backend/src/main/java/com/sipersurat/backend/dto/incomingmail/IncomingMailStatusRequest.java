package com.sipersurat.backend.dto.incomingmail;

import com.sipersurat.backend.enums.IncomingMailStatus;
import jakarta.validation.constraints.NotNull;

public record IncomingMailStatusRequest(

        @NotNull(message = "Status surat wajib dipilih")
        IncomingMailStatus status

) {
}