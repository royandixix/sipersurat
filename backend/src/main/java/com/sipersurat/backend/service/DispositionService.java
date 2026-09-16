package com.sipersurat.backend.service;

import com.sipersurat.backend.dto.disposition.DispositionHistoryResponse;
import com.sipersurat.backend.dto.disposition.DispositionRequest;
import com.sipersurat.backend.dto.disposition.DispositionResponse;
import com.sipersurat.backend.entity.Disposition;
import com.sipersurat.backend.entity.DispositionHistory;
import com.sipersurat.backend.entity.IncomingMail;
import com.sipersurat.backend.enums.DispositionStatus;
import com.sipersurat.backend.enums.DispositionTargetType;
import com.sipersurat.backend.enums.IncomingMailStatus;
import com.sipersurat.backend.exception.BadRequestException;
import com.sipersurat.backend.exception.ResourceNotFoundException;
import com.sipersurat.backend.repository.DispositionRepository;
import com.sipersurat.backend.repository.IncomingMailRepository;
import com.sipersurat.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DispositionService {
    private final DispositionRepository repository;
    private final IncomingMailRepository incomingMailRepository;
    private final UserRepository userRepository;
    private final NumberingService numberingService;

    public DispositionService(
            DispositionRepository repository,
            IncomingMailRepository incomingMailRepository,
            UserRepository userRepository,
            NumberingService numberingService) {
        this.repository = repository;
        this.incomingMailRepository = incomingMailRepository;
        this.userRepository = userRepository;
        this.numberingService = numberingService;
    }

    @Transactional(readOnly = true)
    public List<DispositionResponse> getAll() {
        return repository.findAllWithMail().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DispositionResponse getById(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public DispositionResponse create(DispositionRequest request) {
        IncomingMail mail = findMail(request.incomingMailId());
        validateTarget(request);
        if (mail.getStatus() == IncomingMailStatus.ARCHIVED) {
            throw new BadRequestException("Surat yang telah diarsipkan tidak dapat didisposisikan");
        }
        Disposition disposition = new Disposition();
        disposition.setCode(nextCode());
        disposition.setIncomingMail(mail);
        disposition.setCreatedBy(currentActor());
        apply(disposition, request);
        addHistory(disposition, request.status(), "Disposisi dibuat dan diteruskan kepada " + request.targetName().trim() + ".");
        Disposition saved = repository.save(disposition);
        syncIncomingStatus(mail.getId());
        return toResponse(saved);
    }

    @Transactional
    public DispositionResponse update(Long id, DispositionRequest request) {
        Disposition disposition = find(id);
        Long oldMailId = disposition.getIncomingMail().getId();
        IncomingMail mail = findMail(request.incomingMailId());
        validateTarget(request);
        DispositionStatus oldStatus = disposition.getStatus();
        disposition.setIncomingMail(mail);
        apply(disposition, request);
        if (oldStatus != request.status()) {
            addHistory(disposition, request.status(), statusNote(request.status()));
        }
        Disposition saved = repository.save(disposition);
        syncIncomingStatus(oldMailId);
        if (!oldMailId.equals(mail.getId())) syncIncomingStatus(mail.getId());
        return toResponse(saved);
    }

    @Transactional
    public DispositionResponse updateStatus(Long id, DispositionStatus status) {
        Disposition disposition = find(id);
        if (status == null) throw new BadRequestException("Status wajib dipilih");
        if (disposition.getStatus() != status) {
            disposition.setStatus(status);
            addHistory(disposition, status, statusNote(status));
        }
        Disposition saved = repository.save(disposition);
        syncIncomingStatus(disposition.getIncomingMail().getId());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Disposition disposition = find(id);
        Long mailId = disposition.getIncomingMail().getId();
        repository.delete(disposition);
        repository.flush();
        syncIncomingStatus(mailId);
    }

    private void apply(Disposition disposition, DispositionRequest request) {
        disposition.setTargetType(request.targetType());
        disposition.setTargetId(blankToNull(request.targetId()));
        disposition.setTargetName(request.targetName().trim());
        disposition.setInstruction(request.instruction().trim());
        disposition.setPriority(request.priority());
        disposition.setDueDate(request.dueDate());
        disposition.setNotes(request.notes() == null ? "" : request.notes().trim());
        disposition.setStatus(request.status());
    }

    private void validateTarget(DispositionRequest request) {
        if (request.targetType() == DispositionTargetType.USER) {
            if (request.targetId() == null || request.targetId().isBlank()) {
                throw new BadRequestException("Pengguna tujuan wajib dipilih");
            }
            try {
                Long userId = Long.valueOf(request.targetId());
                if (!userRepository.existsById(userId)) throw new BadRequestException("Pengguna tujuan tidak ditemukan");
            } catch (NumberFormatException ex) {
                throw new BadRequestException("ID pengguna tujuan tidak valid");
            }
        }
    }

    private void addHistory(Disposition disposition, DispositionStatus status, String note) {
        DispositionHistory history = new DispositionHistory();
        history.setDisposition(disposition);
        history.setStatus(status);
        history.setNote(note);
        disposition.getHistory().add(history);
    }

    private void syncIncomingStatus(Long mailId) {
        IncomingMail mail = findMail(mailId);
        if (mail.getStatus() == IncomingMailStatus.ARCHIVED) return;
        List<Disposition> items = repository.findByIncomingMailId(mailId);
        IncomingMailStatus status;
        if (items.isEmpty()) {
            status = IncomingMailStatus.PENDING_DISPOSITION;
        } else if (items.stream().allMatch(item -> item.getStatus() == DispositionStatus.COMPLETED)) {
            status = IncomingMailStatus.COMPLETED;
        } else if (items.stream().anyMatch(item -> item.getStatus() == DispositionStatus.IN_PROGRESS)) {
            status = IncomingMailStatus.IN_PROGRESS;
        } else {
            status = IncomingMailStatus.DISPOSITIONED;
        }
        mail.setStatus(status);
        incomingMailRepository.save(mail);
    }

    private String nextCode() {
        NumberingService.Rule rule = numberingService.disposition();
        long lastNumber = repository.findTopByCodeStartingWithOrderByCodeDesc(rule.leadingPrefix())
                .map(Disposition::getCode)
                .map(rule::sequenceFrom)
                .orElse(0L);
        long number = Math.max(lastNumber + 1, rule.startNumber());
        String code = rule.format(number);
        while (repository.existsByCode(code)) {
            code = rule.format(++number);
        }
        return code;
    }

    private String currentActor() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return "Sistem";
        return userRepository.findByEmailIgnoreCase(auth.getName()).map(user -> user.getName()).orElse(auth.getName());
    }

    private Disposition find(Long id) {
        return repository.findByIdWithMail(id).orElseThrow(() -> new ResourceNotFoundException("Disposisi tidak ditemukan"));
    }

    private IncomingMail findMail(Long id) {
        return incomingMailRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Surat masuk tidak ditemukan"));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String statusNote(DispositionStatus status) {
        return switch (status) {
            case WAITING -> "Disposisi menunggu penerima.";
            case RECEIVED -> "Disposisi telah diterima oleh tujuan.";
            case IN_PROGRESS -> "Tindak lanjut disposisi mulai diproses.";
            case COMPLETED -> "Tindak lanjut disposisi telah diselesaikan.";
        };
    }

    private DispositionResponse toResponse(Disposition disposition) {
        IncomingMail mail = disposition.getIncomingMail();
        List<DispositionHistoryResponse> history = disposition.getHistory().stream()
                .map(item -> new DispositionHistoryResponse(item.getId(), item.getStatus(), item.getNote(), item.getCreatedAt()))
                .toList();
        return new DispositionResponse(
                disposition.getId(), disposition.getCode(), mail.getId(), mail.getAgendaNumber(), mail.getLetterNumber(),
                mail.getSender(), mail.getSubject(), disposition.getTargetType(), disposition.getTargetId(),
                disposition.getTargetName(), disposition.getInstruction(), disposition.getPriority(), disposition.getDueDate(),
                disposition.getNotes(), disposition.getStatus(), disposition.getCreatedBy(), disposition.getCreatedAt(),
                disposition.getUpdatedAt(), history);
    }
}
