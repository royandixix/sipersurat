package com.sipersurat.backend.service;

import com.sipersurat.backend.dto.archive.ArchiveResponse;
import com.sipersurat.backend.entity.IncomingMail;
import com.sipersurat.backend.entity.OutgoingMail;
import com.sipersurat.backend.enums.IncomingMailStatus;
import com.sipersurat.backend.enums.OutgoingMailStatus;
import com.sipersurat.backend.exception.BadRequestException;
import com.sipersurat.backend.exception.ResourceNotFoundException;
import com.sipersurat.backend.repository.IncomingMailRepository;
import com.sipersurat.backend.repository.OutgoingMailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ArchiveService {
    private final IncomingMailRepository incomingRepository;
    private final OutgoingMailRepository outgoingRepository;

    public ArchiveService(IncomingMailRepository incomingRepository, OutgoingMailRepository outgoingRepository) {
        this.incomingRepository = incomingRepository;
        this.outgoingRepository = outgoingRepository;
    }

    @Transactional(readOnly = true)
    public List<ArchiveResponse> getAll() {
        List<ArchiveResponse> result = new ArrayList<>();
        incomingRepository.findAll().stream()
                .filter(item -> item.getStatus() == IncomingMailStatus.ARCHIVED)
                .map(this::incomingResponse).forEach(result::add);
        outgoingRepository.findAll().stream()
                .filter(item -> item.getStatus() == OutgoingMailStatus.ARCHIVED)
                .map(this::outgoingResponse).forEach(result::add);
        result.sort(Comparator.comparing(ArchiveResponse::archiveDate).reversed());
        return result;
    }

    @Transactional
    public ArchiveResponse restore(String source, Long sourceId) {
        if ("INCOMING".equalsIgnoreCase(source)) {
            IncomingMail mail = incomingRepository.findById(sourceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Surat masuk tidak ditemukan"));
            if (mail.getStatus() != IncomingMailStatus.ARCHIVED) throw new BadRequestException("Surat tidak berada di arsip");
            mail.setStatus(IncomingMailStatus.COMPLETED);
            incomingRepository.save(mail);
            return incomingResponse(mail);
        }
        if ("OUTGOING".equalsIgnoreCase(source)) {
            OutgoingMail mail = outgoingRepository.findById(sourceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Surat keluar tidak ditemukan"));
            if (mail.getStatus() != OutgoingMailStatus.ARCHIVED) throw new BadRequestException("Surat tidak berada di arsip");
            mail.setStatus(OutgoingMailStatus.SENT);
            outgoingRepository.save(mail);
            return outgoingResponse(mail);
        }
        throw new BadRequestException("Sumber arsip tidak valid");
    }

    @Transactional
    public void deletePermanent(String source, Long sourceId) {
        if ("INCOMING".equalsIgnoreCase(source)) {
            IncomingMail mail = incomingRepository.findById(sourceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Surat masuk tidak ditemukan"));
            if (mail.getStatus() != IncomingMailStatus.ARCHIVED) throw new BadRequestException("Hanya surat yang diarsipkan yang dapat dihapus permanen");
            incomingRepository.delete(mail);
            return;
        }
        if ("OUTGOING".equalsIgnoreCase(source)) {
            OutgoingMail mail = outgoingRepository.findById(sourceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Surat keluar tidak ditemukan"));
            if (mail.getStatus() != OutgoingMailStatus.ARCHIVED) throw new BadRequestException("Hanya surat yang diarsipkan yang dapat dihapus permanen");
            outgoingRepository.delete(mail);
            return;
        }
        throw new BadRequestException("Sumber arsip tidak valid");
    }

    private ArchiveResponse incomingResponse(IncomingMail item) {
        return new ArchiveResponse(
                "INCOMING-" + item.getId(), item.getId(), "INCOMING", item.getAgendaNumber(), item.getLetterNumber(),
                item.getLetterDate().toString(), item.getUpdatedAt().toLocalDate().toString(), item.getSender(), item.getSubject(),
                safe(item.getCategory()), safe(item.getPriority()), safe(item.getTargetUnit()), safe(item.getNotes()),
                safe(item.getFileName()), safe(item.getFileType()), item.getFileSize() == null ? 0L : item.getFileSize());
    }

    private ArchiveResponse outgoingResponse(OutgoingMail item) {
        return new ArchiveResponse(
                "OUTGOING-" + item.getId(), item.getId(), "OUTGOING", item.getAgendaNumber(), item.getLetterNumber(),
                item.getLetterDate().toString(), item.getUpdatedAt().toLocalDate().toString(), item.getRecipient(), item.getSubject(),
                safe(item.getCategory()), safe(item.getPriority()), safe(item.getSourceUnit()), safe(item.getNotes()),
                safe(item.getFileName()), safe(item.getFileType()), item.getFileSize() == null ? 0L : item.getFileSize());
    }

    private String safe(String value) { return value == null ? "" : value; }
}
