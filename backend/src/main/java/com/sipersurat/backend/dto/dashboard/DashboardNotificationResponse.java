package com.sipersurat.backend.dto.dashboard;

public record DashboardNotificationResponse(String type, String title, String description, long count) {
}