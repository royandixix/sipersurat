package com.sipersurat.backend.dto.user;

import com.sipersurat.backend.enums.UserRole;
import com.sipersurat.backend.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Nama lengkap wajib diisi") @Size(max = 150, message = "Nama maksimal 150 karakter") String name,

        @NotBlank(message = "Email wajib diisi") @Email(message = "Format email tidak valid") @Size(max = 255, message = "Email maksimal 255 karakter") String email,

        @NotNull(message = "Role wajib dipilih") UserRole role,

        @NotBlank(message = "Unit kerja wajib diisi") @Size(max = 150, message = "Unit kerja maksimal 150 karakter") String department,

        @NotNull(message = "Status wajib dipilih") UserStatus status,

        String password) {
}