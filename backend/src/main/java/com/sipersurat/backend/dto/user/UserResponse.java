package com.sipersurat.backend.dto.user;

import com.sipersurat.backend.enums.UserRole;
import com.sipersurat.backend.enums.UserStatus;
import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role,
        String department,
        UserStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime lastLoginAt) {
}