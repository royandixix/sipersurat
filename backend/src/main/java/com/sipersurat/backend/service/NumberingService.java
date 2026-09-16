package com.sipersurat.backend.service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.sipersurat.backend.repository.SystemSettingRepository;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class NumberingService {
    private static final String SETTINGS_KEY = "application";
    private final SystemSettingRepository settingRepository;
    private final ObjectMapper objectMapper;

    public NumberingService(SystemSettingRepository settingRepository, ObjectMapper objectMapper) {
        this.settingRepository = settingRepository;
        this.objectMapper = objectMapper;
    }

    public Rule incoming() {
        return rule("incomingPrefix", "SM", "incomingStartNumber", 1L);
    }

    public Rule outgoing() {
        return rule("outgoingPrefix", "SK", "outgoingStartNumber", 1L);
    }

    public Rule disposition() {
        return rule("dispositionPrefix", "DSP", "dispositionStartNumber", 1L);
    }

    private Rule rule(String prefixField, String defaultPrefix, String startField, long defaultStart) {
        JsonNode settings = readSettings();
        JsonNode numbering = settings.path("numbering");
        JsonNode agenda = settings.path("agenda");

        String prefix = normalizePrefix(numbering.path(prefixField).asString(defaultPrefix), defaultPrefix);
        String separator = normalizeSeparator(numbering.path("separator").asString("-"));
        int padding = clamp(numbering.path("padding").asInt(4), 1, 10);
        boolean resetYearly = numbering.path("resetYearly").asBoolean(true);
        int activeYear = numbering.path("activeYear").asInt(Year.now().getValue());
        if (activeYear < 2000 || activeYear > 9999) activeYear = Year.now().getValue();
        long startNumber = Math.max(1L, agenda.path(startField).asLong(defaultStart));

        String leadingPrefix = resetYearly
                ? prefix + separator + activeYear + separator
                : prefix + separator;

        return new Rule(leadingPrefix, separator, padding, startNumber);
    }

    private JsonNode readSettings() {
        return settingRepository.findById(SETTINGS_KEY)
                .map(item -> parse(item.getValue()))
                .orElseGet(() -> objectMapper.createObjectNode());
    }

    private JsonNode parse(String value) {
        if (value == null || value.isBlank()) return objectMapper.createObjectNode();
        try {
            return objectMapper.readTree(value);
        } catch (JacksonException exception) {
            return objectMapper.createObjectNode();
        }
    }

    private String normalizePrefix(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        String normalized = value.trim().replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        return normalized.isBlank() ? fallback : normalized;
    }

    private String normalizeSeparator(String value) {
        return switch (value) {
            case "/" -> "/";
            case "." -> ".";
            default -> "-";
        };
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public record Rule(String leadingPrefix, String separator, int padding, long startNumber) {
        public String format(long sequence) {
            return leadingPrefix + String.format("%0" + padding + "d", sequence);
        }

        public long sequenceFrom(String value) {
            if (value == null || value.isBlank()) return 0L;
            int index = value.lastIndexOf(separator);
            if (index < 0 || index == value.length() - separator.length()) return 0L;
            try {
                return Long.parseLong(value.substring(index + separator.length()));
            } catch (NumberFormatException exception) {
                return 0L;
            }
        }
    }
}
