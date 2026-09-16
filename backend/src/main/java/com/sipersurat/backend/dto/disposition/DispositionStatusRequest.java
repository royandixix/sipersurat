package com.sipersurat.backend.dto.disposition;
import com.sipersurat.backend.enums.DispositionStatus;
import jakarta.validation.constraints.NotNull;
public record DispositionStatusRequest(@NotNull DispositionStatus status){}
