package com.sipersurat.backend.dto.archive;

public record ArchiveResponse(String id, Long sourceId, String source, String agendaNumber, String letterNumber,
        String letterDate, String archiveDate, String correspondent, String subject, String category, String priority,
        String unit, String notes, String fileName, String fileType, long fileSize) {
}
