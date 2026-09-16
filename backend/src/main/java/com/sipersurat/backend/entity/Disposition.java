package com.sipersurat.backend.entity;

import com.sipersurat.backend.enums.DispositionPriority;
import com.sipersurat.backend.enums.DispositionStatus;
import com.sipersurat.backend.enums.DispositionTargetType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dispositions")
public class Disposition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incoming_mail_id", nullable = false)
    private IncomingMail incomingMail;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 50)
    private DispositionTargetType targetType;

    @Column(name = "target_id", length = 100)
    private String targetId;

    @Column(name = "target_name", nullable = false)
    private String targetName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instruction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DispositionPriority priority;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DispositionStatus status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "disposition", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<DispositionHistory> history = new ArrayList<>();

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (priority == null) priority = DispositionPriority.NORMAL;
        if (status == null) status = DispositionStatus.WAITING;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public IncomingMail getIncomingMail() { return incomingMail; }
    public void setIncomingMail(IncomingMail incomingMail) { this.incomingMail = incomingMail; }
    public DispositionTargetType getTargetType() { return targetType; }
    public void setTargetType(DispositionTargetType targetType) { this.targetType = targetType; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }
    public DispositionPriority getPriority() { return priority; }
    public void setPriority(DispositionPriority priority) { this.priority = priority; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public DispositionStatus getStatus() { return status; }
    public void setStatus(DispositionStatus status) { this.status = status; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public List<DispositionHistory> getHistory() { return history; }
}
