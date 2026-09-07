package com.sipersurat.backend.dto.dashboard;

import java.time.OffsetDateTime;

public record DashboardActivityResponse(String initials, String name, String description, String value,
        OffsetDateTime createdAt) {
}