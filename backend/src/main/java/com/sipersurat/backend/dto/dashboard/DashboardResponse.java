package com.sipersurat.backend.dto.dashboard;

import java.util.List;

public record DashboardResponse(int year, List<DashboardStatisticResponse> statistics,
        List<DashboardChartResponse> chartData, List<DashboardActivityResponse> recentActivity,
        List<DashboardNotificationResponse> notifications) {
}