package com.sipersurat.backend.entity;

import com.sipersurat.backend.enums.MasterDataStatus;
import com.sipersurat.backend.enums.MasterDataType;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "master_data", uniqueConstraints = @UniqueConstraint(columnNames = { "type", "code" }))
public class MasterData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MasterDataType type;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(nullable = false, length = 100)
    private String code;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MasterDataStatus status;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null)
            status = MasterDataStatus.ACTIVE;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public MasterDataType getType() {
        return type;
    }

    public void setType(MasterDataType v) {
        type = v;
    }

    public String getName() {
        return name;
    }

    public void setName(String v) {
        name = v;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String v) {
        code = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        description = v;
    }

    public MasterDataStatus getStatus() {
        return status;
    }

    public void setStatus(MasterDataStatus v) {
        status = v;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
