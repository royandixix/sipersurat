package com.sipersurat.backend.dto.user;

import com.sipersurat.backend.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull(message = "Status wajib diisi") UserStatus status) {
}