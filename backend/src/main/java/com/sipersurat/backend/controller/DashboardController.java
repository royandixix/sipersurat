package com.sipersurat.backend.controller;

import com.sipersurat.backend.dto.dashboard.DashboardActivityResponse;
import com.sipersurat.backend.dto.dashboard.DashboardChartResponse;
import com.sipersurat.backend.dto.dashboard.DashboardNotificationResponse;
import com.sipersurat.backend.dto.dashboard.DashboardResponse;
import com.sipersurat.backend.dto.dashboard.DashboardStatisticResponse;
import com.sipersurat.backend.response.ApiResponse;
import com.sipersurat.backend.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<DashboardResponse> dashboard(@RequestParam(name = "year", required = false) Integer year) {
        return ApiResponse.ok("Dashboard berhasil dimuat", service.getDashboard(year));
    }

    @GetMapping("/statistics")
    public ApiResponse<List<DashboardStatisticResponse>> statistics() {
        return ApiResponse.ok("Statistik berhasil dimuat", service.getStatistics());
    }

    @GetMapping("/chart")
    public ApiResponse<List<DashboardChartResponse>> chart(
            @RequestParam(name = "year", required = false) Integer year) {
        return ApiResponse.ok("Grafik berhasil dimuat", service.getChart(year));
    }

    @GetMapping("/activities")
    public ApiResponse<List<DashboardActivityResponse>> activities() {
        return ApiResponse.ok("Aktivitas terbaru berhasil dimuat", service.getRecentActivities());
    }

    @GetMapping("/notifications")
    public ApiResponse<List<DashboardNotificationResponse>> notifications() {
        return ApiResponse.ok("Notifikasi berhasil dimuat", service.getNotifications());
    }
}