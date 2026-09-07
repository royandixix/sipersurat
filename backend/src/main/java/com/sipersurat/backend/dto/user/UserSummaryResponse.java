package com.sipersurat.backend.dto.user;

public record UserSummaryResponse(
long total,
long active,
long inactive,
long superAdmin
){}
