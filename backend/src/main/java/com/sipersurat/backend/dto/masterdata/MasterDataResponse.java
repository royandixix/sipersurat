package com.sipersurat.backend.dto.masterdata;
import com.sipersurat.backend.enums.*;
import java.time.OffsetDateTime;
public record MasterDataResponse(Long id,MasterDataType type,String name,String code,String description,MasterDataStatus status,long usageCount,OffsetDateTime createdAt,OffsetDateTime updatedAt){}
