package com.sipersurat.backend.service;

import com.sipersurat.backend.dto.outgoingmail.OutgoingMailRequest;
import com.sipersurat.backend.dto.outgoingmail.OutgoingMailResponse;
import com.sipersurat.backend.entity.OutgoingMail;
import com.sipersurat.backend.enums.OutgoingMailStatus;
import com.sipersurat.backend.exception.BadRequestException;
import com.sipersurat.backend.exception.DuplicateResourceException;
import com.sipersurat.backend.exception.ResourceNotFoundException;
import com.sipersurat.backend.repository.OutgoingMailRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
public class OutgoingMailService{
    private final OutgoingMailRepository repository;

    public OutgoingMailService(OutgoingMailRepository repository){
        this.repository=repository;
    }

    @Transactional(readOnly=true)
    public List<OutgoingMailResponse> getAll(){
        return repository.findAll(
                Sort.by(Sort.Direction.DESC,"createdAt")
        ).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly=true)
    public OutgoingMailResponse getById(Long id){
        return toResponse(findById(id));
    }

    @Transactional
    public OutgoingMailResponse create(OutgoingMailRequest request){
        String letterNumber=normalizeRequired(
                request.letterNumber(),
                "Nomor surat wajib diisi"
        );

        if(repository.existsByLetterNumberIgnoreCase(letterNumber)){
            throw new DuplicateResourceException(
                    "Nomor surat sudah terdaftar"
            );
        }

        OutgoingMail mail=new OutgoingMail();
        mail.setAgendaNumber(generateAgendaNumber());

        applyRequest(
                mail,
                request,
                letterNumber
        );

        return toResponse(
                repository.saveAndFlush(mail)
        );
    }

    @Transactional
    public OutgoingMailResponse update(
            Long id,
            OutgoingMailRequest request
    ){
        OutgoingMail mail=findById(id);

        String letterNumber=normalizeRequired(
                request.letterNumber(),
                "Nomor surat wajib diisi"
        );

        if(repository.existsByLetterNumberIgnoreCaseAndIdNot(letterNumber,id)){
            throw new DuplicateResourceException(
                    "Nomor surat sudah digunakan"
            );
        }

        applyRequest(
                mail,
                request,
                letterNumber
        );

        return toResponse(
                repository.saveAndFlush(mail)
        );
    }

    @Transactional
    public OutgoingMailResponse updateStatus(
            Long id,
            OutgoingMailStatus status
    ){
        if(status==null){
            throw new BadRequestException(
                    "Status surat wajib dipilih"
            );
        }

        OutgoingMail mail=findById(id);
        mail.setStatus(status);

        if(
                status==OutgoingMailStatus.SENT&&
                mail.getSentDate()==null
        ){
            mail.setSentDate(LocalDate.now());
        }

        return toResponse(
                repository.saveAndFlush(mail)
        );
    }

    @Transactional
    public void delete(Long id){
        OutgoingMail mail=findById(id);
        repository.delete(mail);
    }

    private OutgoingMail findById(Long id){
        return repository.findById(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "Surat keluar tidak ditemukan"
                        )
                );
    }

    private void applyRequest(
            OutgoingMail mail,
            OutgoingMailRequest request,
            String letterNumber
    ){
        mail.setLetterNumber(letterNumber);

        mail.setLetterDate(
                request.letterDate()
        );

        mail.setRecipient(
                normalizeRequired(
                        request.recipient(),
                        "Tujuan surat wajib diisi"
                )
        );

        mail.setSubject(
                normalizeRequired(
                        request.subject(),
                        "Perihal wajib diisi"
                )
        );

        mail.setCategory(
                normalizeRequired(
                        request.category(),
                        "Kategori surat wajib dipilih"
                )
        );

        mail.setPriority(
                normalizeRequired(
                        request.priority(),
                        "Sifat surat wajib dipilih"
                )
        );

        mail.setSourceUnit(
                normalizeRequired(
                        request.sourceUnit(),
                        "Unit pembuat wajib dipilih"
                )
        );

        mail.setSigner(
                normalizeRequired(
                        request.signer(),
                        "Penandatangan wajib diisi"
                )
        );

        mail.setNotes(
                normalizeNullable(
                        request.notes()
                )
        );

        mail.setFileName(
                normalizeNullable(
                        request.fileName()
                )
        );

        mail.setFileType(
                normalizeNullable(
                        request.fileType()
                )
        );

        Long fileSize=request.fileSize();

        mail.setFileSize(
                fileSize==null
                        ?0L
                        :Math.max(fileSize,0L)
        );

        OutgoingMailStatus status=
                request.status();

        if(status!=null){
            mail.setStatus(status);
        }else if(mail.getStatus()==null){
            mail.setStatus(
                    OutgoingMailStatus.DRAFT
            );
        }

        mail.setSentDate(
                request.sentDate()
        );

        if(
                mail.getStatus()==OutgoingMailStatus.SENT&&
                mail.getSentDate()==null
        ){
            throw new BadRequestException(
                    "Tanggal kirim wajib diisi untuk surat berstatus Dikirim"
            );
        }
    }

    private String generateAgendaNumber(){
        int year=Year.now().getValue();

        String prefix=
                "SK-"+year+"-";

        long lastNumber=repository
                .findTopByAgendaNumberStartingWithOrderByAgendaNumberDesc(prefix)
                .map(OutgoingMail::getAgendaNumber)
                .map(this::extractAgendaSequence)
                .orElse(0L);

        long nextNumber=
                lastNumber+1;

        return String.format(
                "%s%04d",
                prefix,
                nextNumber
        );
    }

    private long extractAgendaSequence(
            String agendaNumber
    ){
        if(
                agendaNumber==null||
                agendaNumber.isBlank()
        ){
            return 0L;
        }

        int lastDash=
                agendaNumber.lastIndexOf('-');

        if(
                lastDash<0||
                lastDash==agendaNumber.length()-1
        ){
            return 0L;
        }

        try{
            return Long.parseLong(
                    agendaNumber.substring(
                            lastDash+1
                    )
            );
        }catch(NumberFormatException exception){
            return 0L;
        }
    }

    private String normalizeRequired(
            String value,
            String message
    ){
        if(
                value==null||
                value.trim().isEmpty()
        ){
            throw new BadRequestException(
                    message
            );
        }

        return value.trim();
    }

    private String normalizeNullable(
            String value
    ){
        if(value==null){
            return null;
        }

        String result=value.trim();

        return result.isEmpty()
                ?null
                :result;
    }

    private OutgoingMailResponse toResponse(
            OutgoingMail mail
    ){
        return new OutgoingMailResponse(
                mail.getId(),
                mail.getAgendaNumber(),
                mail.getLetterNumber(),
                mail.getLetterDate(),
                mail.getRecipient(),
                mail.getSubject(),
                mail.getCategory(),
                mail.getPriority(),
                mail.getSourceUnit(),
                mail.getSigner(),
                mail.getNotes(),
                mail.getStatus(),
                mail.getSentDate(),
                mail.getFileName(),
                mail.getFileType(),
                mail.getFileSize(),
                mail.getCreatedAt(),
                mail.getUpdatedAt()
        );
    }
}