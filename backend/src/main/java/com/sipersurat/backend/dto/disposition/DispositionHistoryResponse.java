package com.sipersurat.backend.dto.disposition;
import com.sipersurat.backend.enums.DispositionStatus;
import java.time.OffsetDateTime;
public record DispositionHistoryResponse(Long id,DispositionStatus status,String note,OffsetDateTime at){}
