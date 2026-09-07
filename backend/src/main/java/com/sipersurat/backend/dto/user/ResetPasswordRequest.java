package com.sipersurat.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Password baru wajib diisi") @Size(min = 8, max = 100, message = "Password minimal 8 karakter") String newPassword) {
}