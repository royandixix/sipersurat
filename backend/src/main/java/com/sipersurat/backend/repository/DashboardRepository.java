package com.sipersurat.backend.repository;

import com.sipersurat.backend.dto.dashboard.DashboardActivityResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DashboardRepository {
    private final JdbcTemplate jdbcTemplate;

    public DashboardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long countIncoming() {
        return count("SELECT COUNT(*) FROM incoming_mails");
    }

    public long countOutgoing() {
        return count("SELECT COUNT(*) FROM outgoing_mails");
    }

    public long countPendingDispositions() {
        return count("SELECT COUNT(*) FROM dispositions WHERE status<>'COMPLETED'");
    }

    public long countUrgentDispositions() {
        return count("SELECT COUNT(*) FROM dispositions WHERE status<>'COMPLETED' AND priority='URGENT'");
    }

    public long countArchives() {
        return count(
                "SELECT (SELECT COUNT(*) FROM incoming_mails WHERE status='ARCHIVED')+(SELECT COUNT(*) FROM outgoing_mails WHERE status='ARCHIVED')");
    }

    public long countIncomingCurrentMonth() {
        return count(
                "SELECT COUNT(*) FROM incoming_mails WHERE created_at>=date_trunc('month',CURRENT_DATE) AND created_at<date_trunc('month',CURRENT_DATE)+INTERVAL '1 month'");
    }

    public long countIncomingPreviousMonth() {
        return count(
                "SELECT COUNT(*) FROM incoming_mails WHERE created_at>=date_trunc('month',CURRENT_DATE)-INTERVAL '1 month' AND created_at<date_trunc('month',CURRENT_DATE)");
    }

    public long countOutgoingCurrentMonth() {
        return count(
                "SELECT COUNT(*) FROM outgoing_mails WHERE created_at>=date_trunc('month',CURRENT_DATE) AND created_at<date_trunc('month',CURRENT_DATE)+INTERVAL '1 month'");
    }

    public long countOutgoingPreviousMonth() {
        return count(
                "SELECT COUNT(*) FROM outgoing_mails WHERE created_at>=date_trunc('month',CURRENT_DATE)-INTERVAL '1 month' AND created_at<date_trunc('month',CURRENT_DATE)");
    }

    public long countArchivedCurrentMonth() {
        return count(
                "SELECT (SELECT COUNT(*) FROM incoming_mails WHERE status='ARCHIVED' AND updated_at>=date_trunc('month',CURRENT_DATE))+(SELECT COUNT(*) FROM outgoing_mails WHERE status='ARCHIVED' AND updated_at>=date_trunc('month',CURRENT_DATE))");
    }

    public long countOverdueDispositions() {
        return count(
                "SELECT COUNT(*) FROM dispositions WHERE status<>'COMPLETED' AND created_at<NOW()-INTERVAL '24 hours'");
    }

    public long countIncomingToday() {
        return count(
                "SELECT COUNT(*) FROM incoming_mails WHERE created_at>=CURRENT_DATE AND created_at<CURRENT_DATE+INTERVAL '1 day'");
    }

    public long countPendingOutgoingApproval() {
        return count("SELECT COUNT(*) FROM outgoing_mails WHERE status='PENDING_APPROVAL'");
    }

    public long countArchivedToday() {
        return count(
                "SELECT (SELECT COUNT(*) FROM incoming_mails WHERE status='ARCHIVED' AND updated_at>=CURRENT_DATE AND updated_at<CURRENT_DATE+INTERVAL '1 day')+(SELECT COUNT(*) FROM outgoing_mails WHERE status='ARCHIVED' AND updated_at>=CURRENT_DATE AND updated_at<CURRENT_DATE+INTERVAL '1 day')");
    }

    public Map<Integer, Long> monthlyActivity(int year) {
        String sql = "WITH months AS(SELECT generate_series(1,12) AS month),activity AS(SELECT EXTRACT(MONTH FROM created_at)::int AS month,COUNT(*) AS total FROM incoming_mails WHERE EXTRACT(YEAR FROM created_at)=? GROUP BY 1 UNION ALL SELECT EXTRACT(MONTH FROM created_at)::int AS month,COUNT(*) AS total FROM outgoing_mails WHERE EXTRACT(YEAR FROM created_at)=? GROUP BY 1 UNION ALL SELECT EXTRACT(MONTH FROM created_at)::int AS month,COUNT(*) AS total FROM dispositions WHERE EXTRACT(YEAR FROM created_at)=? GROUP BY 1) SELECT months.month,COALESCE(SUM(activity.total),0) AS value FROM months LEFT JOIN activity ON activity.month=months.month GROUP BY months.month ORDER BY months.month";
        List<Map.Entry<Integer, Long>> rows = jdbcTemplate.query(sql,
                (rs, rowNum) -> Map.entry(rs.getInt("month"), rs.getLong("value")), year, year, year);
        Map<Integer, Long> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Long> row : rows)
            result.put(row.getKey(), row.getValue());
        return result;
    }

    public List<DashboardActivityResponse> recentActivities() {
        String sql = "SELECT actor_initials,actor_name,description,value,created_at FROM audit_logs ORDER BY created_at DESC LIMIT 5";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new DashboardActivityResponse(rs.getString("actor_initials"),
                        rs.getString("actor_name"), rs.getString("description"), rs.getString("value"),
                        rs.getObject("created_at", java.time.OffsetDateTime.class)));
    }

    public void insertAuditLog(String actorName, String actorInitials, String action, String description,
            String value) {
        jdbcTemplate.update(
                "INSERT INTO audit_logs(actor_name,actor_initials,action,description,value) VALUES(?,?,?,?,?)",
                actorName, actorInitials, action, description, value);
    }

    private long count(String sql, Object... args) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class, args);
        return value == null ? 0 : value;
    }
}