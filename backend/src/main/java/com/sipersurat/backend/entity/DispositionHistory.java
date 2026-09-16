package com.sipersurat.backend.entity;
import com.sipersurat.backend.enums.DispositionStatus;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
@Entity
@Table(name="disposition_histories")
public class DispositionHistory {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="disposition_id",nullable=false) private Disposition disposition;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=50) private DispositionStatus status;
    @Column(nullable=false,columnDefinition="TEXT") private String note;
    @Column(name="created_at",nullable=false) private OffsetDateTime createdAt;
    @PrePersist void prePersist(){createdAt=OffsetDateTime.now();}
    public Long getId(){return id;} public Disposition getDisposition(){return disposition;} public void setDisposition(Disposition v){disposition=v;} public DispositionStatus getStatus(){return status;} public void setStatus(DispositionStatus v){status=v;} public String getNote(){return note;} public void setNote(String v){note=v;} public OffsetDateTime getCreatedAt(){return createdAt;}
}
