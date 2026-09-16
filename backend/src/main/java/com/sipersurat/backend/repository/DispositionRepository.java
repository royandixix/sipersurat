package com.sipersurat.backend.repository;
import com.sipersurat.backend.entity.Disposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface DispositionRepository extends JpaRepository<Disposition,Long>{
    boolean existsByCode(String code);
    Optional<Disposition> findTopByCodeStartingWithOrderByCodeDesc(String prefix);
    List<Disposition> findByIncomingMailId(Long incomingMailId);
    @Query("select distinct d from Disposition d join fetch d.incomingMail left join fetch d.history order by d.createdAt desc")
    List<Disposition> findAllWithMail();
    @Query("select distinct d from Disposition d join fetch d.incomingMail left join fetch d.history where d.id=:id")
    Optional<Disposition> findByIdWithMail(@Param("id") Long id);
}
