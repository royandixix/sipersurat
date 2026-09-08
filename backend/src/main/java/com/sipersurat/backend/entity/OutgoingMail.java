package com.sipersurat.backend.entity;

import com.sipersurat.backend.enums.OutgoingMailStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name="outgoing_mails")
public class OutgoingMail{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="agenda_number",nullable=false,unique=true)
    private String agendaNumber;

    @Column(name="letter_number",nullable=false)
    private String letterNumber;

    @Column(name="letter_date",nullable=false)
    private LocalDate letterDate;

    @Column(nullable=false)
    private String recipient;

    @Column(nullable=false,columnDefinition="TEXT")
    private String subject;

    private String category;
    private String priority;

    @Column(name="source_unit")
    private String sourceUnit;

    private String signer;

    @Column(columnDefinition="TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private OutgoingMailStatus status;

    @Column(name="sent_date")
    private LocalDate sentDate;

    @Column(name="file_name")
    private String fileName;

    @Column(name="file_type")
    private String fileType;

    @Column(name="file_size")
    private Long fileSize;

    @Column(name="created_at")
    private OffsetDateTime createdAt;

    @Column(name="updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        OffsetDateTime now=OffsetDateTime.now();
        createdAt=now;
        updatedAt=now;
        if(status==null){
            status=OutgoingMailStatus.DRAFT;
        }
        if(fileSize==null){
            fileSize=0L;
        }
    }

    @PreUpdate
    public void preUpdate(){
        updatedAt=OffsetDateTime.now();
    }

    public Long getId(){
        return id;
    }

    public String getAgendaNumber(){
        return agendaNumber;
    }

    public void setAgendaNumber(String agendaNumber){
        this.agendaNumber=agendaNumber;
    }

    public String getLetterNumber(){
        return letterNumber;
    }

    public void setLetterNumber(String letterNumber){
        this.letterNumber=letterNumber;
    }

    public LocalDate getLetterDate(){
        return letterDate;
    }

    public void setLetterDate(LocalDate letterDate){
        this.letterDate=letterDate;
    }

    public String getRecipient(){
        return recipient;
    }

    public void setRecipient(String recipient){
        this.recipient=recipient;
    }

    public String getSubject(){
        return subject;
    }

    public void setSubject(String subject){
        this.subject=subject;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category=category;
    }

    public String getPriority(){
        return priority;
    }

    public void setPriority(String priority){
        this.priority=priority;
    }

    public String getSourceUnit(){
        return sourceUnit;
    }

    public void setSourceUnit(String sourceUnit){
        this.sourceUnit=sourceUnit;
    }

    public String getSigner(){
        return signer;
    }

    public void setSigner(String signer){
        this.signer=signer;
    }

    public String getNotes(){
        return notes;
    }

    public void setNotes(String notes){
        this.notes=notes;
    }

    public OutgoingMailStatus getStatus(){
        return status;
    }

    public void setStatus(OutgoingMailStatus status){
        this.status=status;
    }

    public LocalDate getSentDate(){
        return sentDate;
    }

    public void setSentDate(LocalDate sentDate){
        this.sentDate=sentDate;
    }

    public String getFileName(){
        return fileName;
    }

    public void setFileName(String fileName){
        this.fileName=fileName;
    }

    public String getFileType(){
        return fileType;
    }

    public void setFileType(String fileType){
        this.fileType=fileType;
    }

    public Long getFileSize(){
        return fileSize;
    }

    public void setFileSize(Long fileSize){
        this.fileSize=fileSize;
    }

    public OffsetDateTime getCreatedAt(){
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt(){
        return updatedAt;
    }
}