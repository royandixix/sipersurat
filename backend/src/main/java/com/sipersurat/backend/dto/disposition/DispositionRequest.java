package com.sipersurat.backend.dto.disposition;
import com.sipersurat.backend.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
public record DispositionRequest(@NotNull Long incomingMailId,@NotNull DispositionTargetType targetType,String targetId,@NotBlank String targetName,@NotBlank String instruction,@NotNull DispositionPriority priority,LocalDate dueDate,String notes,@NotNull DispositionStatus status){}
