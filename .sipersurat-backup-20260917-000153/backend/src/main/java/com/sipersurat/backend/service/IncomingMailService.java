package com.sipersurat.backend.service;

import com.sipersurat.backend.dto.incomingmail.IncomingMailRequest;
import com.sipersurat.backend.dto.incomingmail.IncomingMailResponse;
import com.sipersurat.backend.entity.IncomingMail;
import com.sipersurat.backend.enums.IncomingMailStatus;
import com.sipersurat.backend.exception.BadRequestException;
import com.sipersurat.backend.exception.DuplicateResourceException;
import com.sipersurat.backend.exception.ResourceNotFoundException;
import com.sipersurat.backend.repository.IncomingMailRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
public class IncomingMailService{
    private final IncomingMailRepository repository;

    public IncomingMailService(IncomingMailRepository repository){
        this.repository=repository;
    }

    @Transactional(readOnly=true)
    public List<IncomingMailResponse> getAll(){
        return repository.findAll(
                Sort.by(Sort.Direction.DESC,"createdAt")
        ).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly=true)
    public IncomingMailResponse getById(Long id){
        return toResponse(findById(id));
    }

    @Transactional
    public IncomingMailResponse create(IncomingMailRequest request){
        String letterNumber=normalizeRequired(
                request.letterNumber(),
                "Nomor surat wajib diisi"
        );

        if(repository.existsByLetterNumberIgnoreCase(letterNumber)){
            throw new DuplicateResourceException(
                    "Nomor surat sudah terdaftar"
            );
        }

        IncomingMail mail=new IncomingMail();

        mail.setAgendaNumber(generateAgendaNumber());

        applyRequest(
                mail,
                request,
                letterNumber
        );

        IncomingMail saved=repository.save(mail);

        return toResponse(saved);
    }

    @Transactional
    public IncomingMailResponse update(Long id,IncomingMailRequest request){
        IncomingMail mail=findById(id);

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

        IncomingMail saved=repository.save(mail);

        return toResponse(saved);
    }

    @Transactional
    public IncomingMailResponse updateStatus(Long id,IncomingMailStatus status){
        if(status==null){
            throw new BadRequestException(
                    "Status surat wajib dipilih"
            );
        }

        IncomingMail mail=findById(id);

        mail.setStatus(status);

        return toResponse(
                repository.save(mail)
        );
    }

    @Transactional
    public void delete(Long id){
        IncomingMail mail=findById(id);
        repository.delete(mail);
    }

    private IncomingMail findById(Long id){
        return repository.findById(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "Surat masuk tidak ditemukan"
                        )
                );
    }

    private void applyRequest(
            IncomingMail mail,
            IncomingMailRequest request,
            String letterNumber
    ){
        mail.setLetterNumber(letterNumber);

        mail.setLetterDate(
                request.letterDate()
        );

        mail.setReceivedDate(
                request.receivedDate()
        );

        mail.setSender(
                normalizeRequired(
                        request.sender(),
                        "Pengirim wajib diisi"
                )
        );

        mail.setSubject(
                normalizeRequired(
                        request.subject(),
                        "Perihal wajib diisi"
                )
        );

        mail.setCategory(
                normalizeNullable(
                        request.category()
                )
        );

        mail.setPriority(
                normalizeNullable(
                        request.priority()
                )
        );

        mail.setTargetUnit(
                normalizeNullable(
                        request.targetUnit()
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

        if(request.status()!=null){
            mail.setStatus(
                    request.status()
            );
        }else if(mail.getStatus()==null){
            mail.setStatus(
                    IncomingMailStatus.RECEIVED
            );
        }
    }

    private String generateAgendaNumber(){
        int year=Year.now().getValue();

        String prefix=
                "SM-"+year+"-";

        long lastNumber=repository
                .findTopByAgendaNumberStartingWithOrderByAgendaNumberDesc(prefix)
                .map(IncomingMail::getAgendaNumber)
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

    private long extractAgendaSequence(String agendaNumber){
        if(agendaNumber==null||agendaNumber.isBlank()){
            return 0L;
        }

        int lastDash=
                agendaNumber.lastIndexOf('-');

        if(lastDash<0||lastDash==agendaNumber.length()-1){
            return 0L;
        }

        try{
            return Long.parseLong(
                    agendaNumber.substring(lastDash+1)
            );
        }catch(NumberFormatException exception){
            return 0L;
        }
    }

    private String normalizeRequired(String value,String message){
        if(value==null||value.trim().isEmpty()){
            throw new BadRequestException(message);
        }

        return value.trim();
    }

    private String normalizeNullable(String value){
        if(value==null){
            return null;
        }

        String result=value.trim();

        return result.isEmpty()
                ?null
                :result;
    }

    private IncomingMailResponse toResponse(IncomingMail mail){
        return new IncomingMailResponse(
                mail.getId(),
                mail.getAgendaNumber(),
                mail.getLetterNumber(),
                mail.getLetterDate(),
                mail.getReceivedDate(),
                mail.getSender(),
                mail.getSubject(),
                mail.getCategory(),
                mail.getPriority(),
                mail.getTargetUnit(),
                mail.getNotes(),
                mail.getStatus(),
                mail.getFileName(),
                mail.getFileType(),
                mail.getFileSize(),
                mail.getCreatedAt(),
                mail.getUpdatedAt()
        );
    }
}