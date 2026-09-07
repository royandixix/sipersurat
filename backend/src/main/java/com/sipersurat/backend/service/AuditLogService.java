package com.sipersurat.backend.service;

import com.sipersurat.backend.repository.DashboardRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {
    private final DashboardRepository repository;

    public AuditLogService(DashboardRepository repository) {
        this.repository = repository;
    }

    public void record(String actorName, String actorInitials, String action, String description, String value) {
        repository.insertAuditLog(actorName, actorInitials, action, description, value);
    }
}