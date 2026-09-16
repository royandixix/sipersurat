package com.sipersurat.backend.dto.masterdata;
import com.sipersurat.backend.enums.MasterDataStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record MasterDataRequest(@NotBlank String name,@NotBlank String code,String description,@NotNull MasterDataStatus status){}
