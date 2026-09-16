package com.sipersurat.backend.repository;

import com.sipersurat.backend.entity.IncomingMail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IncomingMailRepository extends JpaRepository<IncomingMail,Long>{
    boolean existsByLetterNumberIgnoreCase(String letterNumber);

    boolean existsByLetterNumberIgnoreCaseAndIdNot(String letterNumber,Long id);

    Optional<IncomingMail> findTopByAgendaNumberStartingWithOrderByAgendaNumberDesc(String prefix);
   
    long countByTargetUnitIgnoreCase(String targetUnit);
    long countByCategoryIgnoreCase(String category);
    long countByPriorityIgnoreCase(String priority);
}