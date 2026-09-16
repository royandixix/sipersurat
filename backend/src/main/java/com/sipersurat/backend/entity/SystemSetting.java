package com.sipersurat.backend.entity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
@Entity
@Table(name="system_settings")
public class SystemSetting {
    @Id @Column(name="key",length=100) private String key;
    @Column(name="value",nullable=false,columnDefinition="TEXT") private String value;
    @Column(name="updated_at",nullable=false) private OffsetDateTime updatedAt;
    @PrePersist @PreUpdate void touch(){updatedAt=OffsetDateTime.now();}
    public String getKey(){return key;} public void setKey(String v){key=v;} public String getValue(){return value;} public void setValue(String v){value=v;} public OffsetDateTime getUpdatedAt(){return updatedAt;}
}
