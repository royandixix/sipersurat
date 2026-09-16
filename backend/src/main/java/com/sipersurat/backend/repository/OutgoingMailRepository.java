package com.sipersurat.backend.repository;

import com.sipersurat.backend.entity.OutgoingMail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OutgoingMailRepository extends JpaRepository<OutgoingMail,Long>{
    boolean existsByLetterNumberIgnoreCase(String letterNumber);

    boolean existsByLetterNumberIgnoreCaseAndIdNot(String letterNumber,Long id);

    Optional<OutgoingMail> findTopByAgendaNumberStartingWithOrderByAgendaNumberDesc(String prefix);
    long countBySourceUnitIgnoreCase(String sourceUnit);
    long countByCategoryIgnoreCase(String category);
    long countByPriorityIgnoreCase(String priority);
}