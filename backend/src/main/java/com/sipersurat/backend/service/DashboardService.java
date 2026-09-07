package com.sipersurat.backend.service;

import com.sipersurat.backend.dto.dashboard.DashboardActivityResponse;
import com.sipersurat.backend.dto.dashboard.DashboardChartResponse;
import com.sipersurat.backend.dto.dashboard.DashboardNotificationResponse;
import com.sipersurat.backend.dto.dashboard.DashboardResponse;
import com.sipersurat.backend.dto.dashboard.DashboardStatisticResponse;
import com.sipersurat.backend.repository.DashboardRepository;
import org.springframework.stereotype.Service;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    private static final List<String> MONTHS = List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec");
    private final DashboardRepository repository;

    public DashboardService(DashboardRepository repository) {
        this.repository = repository;
    }

    public DashboardResponse getDashboard(Integer year) {
        int selectedYear = resolveYear(year);
        return new DashboardResponse(selectedYear, getStatistics(), getChart(selectedYear), getRecentActivities(),
                getNotifications());
    }

    public List<DashboardStatisticResponse> getStatistics() {
        long incoming = repository.countIncoming();
        long outgoing = repository.countOutgoing();
        long pending = repository.countPendingDispositions();
        long urgent = repository.countUrgentDispositions();
        long archives = repository.countArchives();
        long incomingCurrent = repository.countIncomingCurrentMonth();
        long incomingPrevious = repository.countIncomingPreviousMonth();
        long outgoingCurrent = repository.countOutgoingCurrentMonth();
        long outgoingPrevious = repository.countOutgoingPreviousMonth();
        long archivedCurrent = repository.countArchivedCurrentMonth();
        return List.of(
                new DashboardStatisticResponse("incoming", "Surat Masuk", incoming,
                        differenceText(incomingCurrent, incomingPrevious)),
                new DashboardStatisticResponse("outgoing", "Surat Keluar", outgoing,
                        differenceText(outgoingCurrent, outgoingPrevious)),
                new DashboardStatisticResponse("disposition", "Perlu Disposisi", pending, urgent + " prioritas tinggi"),
                new DashboardStatisticResponse("archive", "Total Arsip", archives,
                        "+" + archivedCurrent + " bulan ini"));
    }

    public List<DashboardChartResponse> getChart(Integer year) {
        int selectedYear = resolveYear(year);
        Map<Integer, Long> data = repository.monthlyActivity(selectedYear);
        List<DashboardChartResponse> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++)
            result.add(new DashboardChartResponse(MONTHS.get(month - 1), data.getOrDefault(month, 0L)));
        return result;
    }

    public List<DashboardActivityResponse> getRecentActivities() {
        return repository.recentActivities();
    }

    public List<DashboardNotificationResponse> getNotifications() {
        List<DashboardNotificationResponse> result = new ArrayList<>();
        long overdue = repository.countOverdueDispositions();
        long incomingToday = repository.countIncomingToday();
        long approval = repository.countPendingOutgoingApproval();
        long archivedToday = repository.countArchivedToday();
        if (overdue > 0)
            result.add(new DashboardNotificationResponse("DISPOSITION", overdue + " disposisi membutuhkan perhatian",
                    "Belum ditindaklanjuti lebih dari 24 jam", overdue));
        if (incomingToday > 0)
            result.add(new DashboardNotificationResponse("INCOMING_MAIL", incomingToday + " surat masuk baru",
                    "Surat diterima hari ini", incomingToday));
        if (approval > 0)
            result.add(new DashboardNotificationResponse("OUTGOING_APPROVAL", approval + " surat menunggu persetujuan",
                    "Surat keluar membutuhkan proses persetujuan", approval));
        if (archivedToday > 0)
            result.add(new DashboardNotificationResponse("ARCHIVE", archivedToday + " dokumen berhasil diarsipkan",
                    "Dokumen masuk arsip digital hari ini", archivedToday));
        if (result.isEmpty())
            result.add(new DashboardNotificationResponse("INFO", "Tidak ada notifikasi baru",
                    "Semua proses persuratan dalam kondisi terkendali", 0));
        return result;
    }

    private int resolveYear(Integer year) {
        return year == null ? Year.now(ZoneId.of("Asia/Makassar")).getValue() : year;
    }

    private String differenceText(long current, long previous) {
        long difference = current - previous;
        if (difference > 0)
            return "+" + difference + " dari bulan lalu";
        if (difference < 0)
            return difference + " dari bulan lalu";
        return "Sama dengan bulan lalu";
    }
}