package com.sipersurat.backend.repository;
import com.sipersurat.backend.entity.MasterData;
import com.sipersurat.backend.enums.MasterDataType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface MasterDataRepository extends JpaRepository<MasterData,Long>{
    List<MasterData> findAllByOrderByTypeAscNameAsc();
    boolean existsByTypeAndCodeIgnoreCase(MasterDataType type,String code);
    boolean existsByTypeAndCodeIgnoreCaseAndIdNot(MasterDataType type,String code,Long id);
}
