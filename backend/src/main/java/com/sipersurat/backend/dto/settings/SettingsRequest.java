package com.sipersurat.backend.dto.settings;
import tools.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
public record SettingsRequest(@NotNull JsonNode settings){}
