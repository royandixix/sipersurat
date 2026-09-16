package com.sipersurat.backend.service;

import com.sipersurat.backend.dto.report.ReportRecordResponse;
import com.sipersurat.backend.entity.Disposition;
import com.sipersurat.backend.entity.IncomingMail;
import com.sipersurat.backend.entity.OutgoingMail;
import com.sipersurat.backend.enums.IncomingMailStatus;
import com.sipersurat.backend.enums.OutgoingMailStatus;
import com.sipersurat.backend.repository.DispositionRepository;
import com.sipersurat.backend.repository.IncomingMailRepository;
import com.sipersurat.backend.repository.OutgoingMailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ReportService {
    private final IncomingMailRepository incomingRepository;
    private final OutgoingMailRepository outgoingRepository;
    private final DispositionRepository dispositionRepository;

    public ReportService(IncomingMailRepository incomingRepository, OutgoingMailRepository outgoingRepository, DispositionRepository dispositionRepository) {
        this.incomingRepository = incomingRepository;
        this.outgoingRepository = outgoingRepository;
        this.dispositionRepository = dispositionRepository;
    }

    @Transactional(readOnly = true)
    public List<ReportRecordResponse> getAll() {
        List<ReportRecordResponse> rows = new ArrayList<>();
        for (IncomingMail item : incomingRepository.findAll()) {
            rows.add(new ReportRecordResponse(
                    "INCOMING-" + item.getId(), "INCOMING", item.getReceivedDate().toString(), item.getAgendaNumber(), item.getLetterNumber(),
                    item.getSender(), item.getSubject(), safeOrDash(item.getCategory()), safeOrDash(item.getTargetUnit()), incomingStatus(item.getStatus())));
            if (item.getStatus() == IncomingMailStatus.ARCHIVED) {
                rows.add(new ReportRecordResponse(
                        "ARCHIVE-INCOMING-" + item.getId(), "ARCHIVE", item.getUpdatedAt().toLocalDate().toString(), item.getAgendaNumber(),
                        item.getLetterNumber(), item.getSender(), item.getSubject(), safeOrDash(item.getCategory()), safeOrDash(item.getTargetUnit()), "Diarsipkan"));
            }
        }
        for (OutgoingMail item : outgoingRepository.findAll()) {
            String date = item.getSentDate() != null ? item.getSentDate().toString() : item.getLetterDate().toString();
            rows.add(new ReportRecordResponse(
                    "OUTGOING-" + item.getId(), "OUTGOING", date, item.getAgendaNumber(), item.getLetterNumber(), item.getRecipient(),
                    item.getSubject(), safeOrDash(item.getCategory()), safeOrDash(item.getSourceUnit()), outgoingStatus(item.getStatus())));
            if (item.getStatus() == OutgoingMailStatus.ARCHIVED) {
                rows.add(new ReportRecordResponse(
                        "ARCHIVE-OUTGOING-" + item.getId(), "ARCHIVE", item.getUpdatedAt().toLocalDate().toString(), item.getAgendaNumber(),
                        item.getLetterNumber(), item.getRecipient(), item.getSubject(), safeOrDash(item.getCategory()), safeOrDash(item.getSourceUnit()), "Diarsipkan"));
            }
        }
        for (Disposition item : dispositionRepository.findAllWithMail()) {
            rows.add(new ReportRecordResponse(
                    "DISPOSITION-" + item.getId(), "DISPOSITION", item.getCreatedAt().toLocalDate().toString(), item.getIncomingMail().getAgendaNumber(),
                    item.getIncomingMail().getLetterNumber(), item.getTargetName(), item.getIncomingMail().getSubject(), "Disposisi",
                    safeOrDash(item.getTargetName()), dispositionStatus(item)));
        }
        rows.sort(Comparator.comparing(ReportRecordResponse::date).reversed());
        return rows;
    }

    private String incomingStatus(IncomingMailStatus status) {
        return switch (status) {
            case RECEIVED -> "Diterima";
            case PENDING_DISPOSITION -> "Menunggu Disposisi";
            case DISPOSITIONED -> "Didisposisikan";
            case IN_PROGRESS -> "Diproses";
            case COMPLETED -> "Selesai";
            case ARCHIVED -> "Diarsipkan";
        };
    }

    private String outgoingStatus(OutgoingMailStatus status) {
        return switch (status) {
            case DRAFT -> "Draft";
            case PENDING_APPROVAL -> "Menunggu Persetujuan";
            case APPROVED -> "Disetujui";
            case SENT -> "Dikirim";
            case REJECTED -> "Ditolak";
            case ARCHIVED -> "Diarsipkan";
        };
    }

    private String dispositionStatus(Disposition item) {
        return switch (item.getStatus()) {
            case WAITING -> "Menunggu Penerima";
            case RECEIVED -> "Diterima";
            case IN_PROGRESS -> "Diproses";
            case COMPLETED -> "Selesai";
        };
    }

    private String safeOrDash(String value) { return value == null || value.isBlank() ? "-" : value; }
}
