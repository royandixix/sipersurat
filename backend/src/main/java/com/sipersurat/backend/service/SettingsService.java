package com.sipersurat.backend.service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import com.sipersurat.backend.entity.*;
import com.sipersurat.backend.enums.*;
import com.sipersurat.backend.exception.BadRequestException;
import com.sipersurat.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SettingsService {
    private static final String SETTINGS_KEY = "application";
    private final SystemSettingRepository settingRepository;
    private final MasterDataRepository masterDataRepository;
    private final IncomingMailRepository incomingRepository;
    private final OutgoingMailRepository outgoingRepository;
    private final DispositionRepository dispositionRepository;
    private final ObjectMapper objectMapper;

    public SettingsService(
            SystemSettingRepository settingRepository,
            MasterDataRepository masterDataRepository,
            IncomingMailRepository incomingRepository,
            OutgoingMailRepository outgoingRepository,
            DispositionRepository dispositionRepository,
            ObjectMapper objectMapper) {
        this.settingRepository = settingRepository;
        this.masterDataRepository = masterDataRepository;
        this.incomingRepository = incomingRepository;
        this.outgoingRepository = outgoingRepository;
        this.dispositionRepository = dispositionRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public JsonNode getSettings() {
        return settingRepository.findById(SETTINGS_KEY)
                .map(item -> parse(item.getValue()))
                .orElseGet(this::defaultSettings);
    }

    @Transactional
    public JsonNode saveSettings(JsonNode input) {
        if (input == null || !input.isObject()) throw new BadRequestException("Format pengaturan tidak valid");
        JsonNode institution = input.path("institution");
        if (institution.path("name").asString("").isBlank()) throw new BadRequestException("Nama instansi wajib diisi");
        ObjectNode settings = ((ObjectNode) input).deepCopy();
        settings.put("updatedAt", OffsetDateTime.now().toString());
        SystemSetting entity = settingRepository.findById(SETTINGS_KEY).orElseGet(SystemSetting::new);
        entity.setKey(SETTINGS_KEY);
        entity.setValue(stringify(settings));
        settingRepository.save(entity);
        return settings;
    }

    @Transactional
    public JsonNode resetSettings() {
        JsonNode defaults = defaultSettings();
        SystemSetting entity = settingRepository.findById(SETTINGS_KEY).orElseGet(SystemSetting::new);
        entity.setKey(SETTINGS_KEY);
        entity.setValue(stringify(defaults));
        settingRepository.save(entity);
        return defaults;
    }

    @Transactional(readOnly = true)
    public JsonNode createBackup() {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("application", "SiPersurat");
        root.put("version", 2);
        root.put("exportedAt", OffsetDateTime.now().toString());
        ObjectNode data = root.putObject("data");
        data.set("settings", getSettings());

        ArrayNode master = data.putArray("masterData");
        masterDataRepository.findAllByOrderByTypeAscNameAsc().forEach(item -> {
            ObjectNode node = master.addObject();
            node.put("type", item.getType().name());
            node.put("name", item.getName());
            node.put("code", item.getCode());
            node.put("description", safe(item.getDescription()));
            node.put("status", item.getStatus().name());
        });

        ArrayNode incoming = data.putArray("incomingMails");
        incomingRepository.findAll().forEach(item -> {
            ObjectNode node = incoming.addObject();
            node.put("oldId", item.getId());
            node.put("agendaNumber", item.getAgendaNumber());
            node.put("letterNumber", item.getLetterNumber());
            node.put("letterDate", item.getLetterDate().toString());
            node.put("receivedDate", item.getReceivedDate().toString());
            node.put("sender", item.getSender());
            node.put("subject", item.getSubject());
            node.put("category", safe(item.getCategory()));
            node.put("priority", safe(item.getPriority()));
            node.put("targetUnit", safe(item.getTargetUnit()));
            node.put("notes", safe(item.getNotes()));
            node.put("status", item.getStatus().name());
            node.put("fileName", safe(item.getFileName()));
            node.put("fileType", safe(item.getFileType()));
            node.put("fileSize", item.getFileSize() == null ? 0 : item.getFileSize());
        });

        ArrayNode outgoing = data.putArray("outgoingMails");
        outgoingRepository.findAll().forEach(item -> {
            ObjectNode node = outgoing.addObject();
            node.put("agendaNumber", item.getAgendaNumber());
            node.put("letterNumber", item.getLetterNumber());
            node.put("letterDate", item.getLetterDate().toString());
            node.put("recipient", item.getRecipient());
            node.put("subject", item.getSubject());
            node.put("category", safe(item.getCategory()));
            node.put("priority", safe(item.getPriority()));
            node.put("sourceUnit", safe(item.getSourceUnit()));
            node.put("signer", safe(item.getSigner()));
            node.put("notes", safe(item.getNotes()));
            node.put("status", item.getStatus().name());
            node.put("sentDate", item.getSentDate() == null ? "" : item.getSentDate().toString());
            node.put("fileName", safe(item.getFileName()));
            node.put("fileType", safe(item.getFileType()));
            node.put("fileSize", item.getFileSize() == null ? 0 : item.getFileSize());
        });

        ArrayNode dispositions = data.putArray("dispositions");
        dispositionRepository.findAllWithMail().forEach(item -> {
            ObjectNode node = dispositions.addObject();
            node.put("code", item.getCode());
            node.put("incomingMailOldId", item.getIncomingMail().getId());
            node.put("targetType", item.getTargetType().name());
            node.put("targetId", safe(item.getTargetId()));
            node.put("targetName", item.getTargetName());
            node.put("instruction", item.getInstruction());
            node.put("priority", item.getPriority().name());
            node.put("dueDate", item.getDueDate() == null ? "" : item.getDueDate().toString());
            node.put("notes", safe(item.getNotes()));
            node.put("status", item.getStatus().name());
            node.put("createdBy", safe(item.getCreatedBy()));
        });
        return root;
    }

    @Transactional
    public JsonNode restoreBackup(JsonNode root) {
        if (root == null || !"SiPersurat".equals(root.path("application").asString()) || !root.path("data").isObject()) {
            throw new BadRequestException("File backup SiPersurat tidak valid");
        }
        JsonNode data = root.path("data");
        dispositionRepository.deleteAll();
        dispositionRepository.flush();
        incomingRepository.deleteAll();
        outgoingRepository.deleteAll();
        masterDataRepository.deleteAll();

        JsonNode master = data.path("masterData");
        if (master.isArray()) {
            for (JsonNode node : master) {
                MasterData item = new MasterData();
                item.setType(enumValue(MasterDataType.class, node.path("type").asString(), MasterDataType.UNIT_KERJA));
                item.setName(node.path("name").asString().trim());
                item.setCode(node.path("code").asString().trim());
                item.setDescription(node.path("description").asString(""));
                item.setStatus(enumValue(MasterDataStatus.class, node.path("status").asString(), MasterDataStatus.ACTIVE));
                if (!item.getName().isBlank() && !item.getCode().isBlank()) masterDataRepository.save(item);
            }
        }

        Map<Long, IncomingMail> mailMap = new HashMap<>();
        JsonNode incoming = data.path("incomingMails");
        if (incoming.isArray()) {
            for (JsonNode node : incoming) {
                IncomingMail item = new IncomingMail();
                item.setAgendaNumber(required(node, "agendaNumber"));
                item.setLetterNumber(required(node, "letterNumber"));
                item.setLetterDate(LocalDate.parse(required(node, "letterDate")));
                item.setReceivedDate(LocalDate.parse(required(node, "receivedDate")));
                item.setSender(required(node, "sender"));
                item.setSubject(required(node, "subject"));
                item.setCategory(node.path("category").asString(""));
                item.setPriority(node.path("priority").asString(""));
                item.setTargetUnit(node.path("targetUnit").asString(""));
                item.setNotes(node.path("notes").asString(""));
                item.setStatus(enumValue(IncomingMailStatus.class, node.path("status").asString(), IncomingMailStatus.RECEIVED));
                item.setFileName(node.path("fileName").asString(""));
                item.setFileType(node.path("fileType").asString(""));
                item.setFileSize(node.path("fileSize").asLong(0));
                IncomingMail saved = incomingRepository.save(item);
                mailMap.put(node.path("oldId").asLong(), saved);
            }
        }

        JsonNode outgoing = data.path("outgoingMails");
        if (outgoing.isArray()) {
            for (JsonNode node : outgoing) {
                OutgoingMail item = new OutgoingMail();
                item.setAgendaNumber(required(node, "agendaNumber"));
                item.setLetterNumber(required(node, "letterNumber"));
                item.setLetterDate(LocalDate.parse(required(node, "letterDate")));
                item.setRecipient(required(node, "recipient"));
                item.setSubject(required(node, "subject"));
                item.setCategory(node.path("category").asString(""));
                item.setPriority(node.path("priority").asString(""));
                item.setSourceUnit(node.path("sourceUnit").asString(""));
                item.setSigner(node.path("signer").asString(""));
                item.setNotes(node.path("notes").asString(""));
                item.setStatus(enumValue(OutgoingMailStatus.class, node.path("status").asString(), OutgoingMailStatus.DRAFT));
                String sentDate = node.path("sentDate").asString("");
                item.setSentDate(sentDate.isBlank() ? null : LocalDate.parse(sentDate));
                item.setFileName(node.path("fileName").asString(""));
                item.setFileType(node.path("fileType").asString(""));
                item.setFileSize(node.path("fileSize").asLong(0));
                outgoingRepository.save(item);
            }
        }

        JsonNode dispositions = data.path("dispositions");
        if (dispositions.isArray()) {
            for (JsonNode node : dispositions) {
                IncomingMail mail = mailMap.get(node.path("incomingMailOldId").asLong());
                if (mail == null) continue;
                Disposition item = new Disposition();
                item.setCode(required(node, "code"));
                item.setIncomingMail(mail);
                item.setTargetType(enumValue(DispositionTargetType.class, node.path("targetType").asString(), DispositionTargetType.UNIT));
                item.setTargetId(blankToNull(node.path("targetId").asString("")));
                item.setTargetName(required(node, "targetName"));
                item.setInstruction(required(node, "instruction"));
                item.setPriority(enumValue(DispositionPriority.class, node.path("priority").asString(), DispositionPriority.NORMAL));
                String dueDate = node.path("dueDate").asString("");
                item.setDueDate(dueDate.isBlank() ? null : LocalDate.parse(dueDate));
                item.setNotes(node.path("notes").asString(""));
                DispositionStatus status = enumValue(DispositionStatus.class, node.path("status").asString(), DispositionStatus.WAITING);
                item.setStatus(status);
                item.setCreatedBy(node.path("createdBy").asString("Sistem"));
                DispositionHistory history = new DispositionHistory();
                history.setDisposition(item);
                history.setStatus(status);
                history.setNote("Data disposisi dipulihkan dari backup.");
                item.getHistory().add(history);
                dispositionRepository.save(item);
            }
        }

        JsonNode settings = data.path("settings");
        if (settings.isObject()) saveSettings(settings);
        return getSettings();
    }

    @Transactional
    public void resetOperationalData() {
        dispositionRepository.deleteAll();
        dispositionRepository.flush();
        incomingRepository.deleteAll();
        outgoingRepository.deleteAll();
    }

    private JsonNode defaultSettings() {
        int year = LocalDate.now().getYear();
        ObjectNode root = objectMapper.createObjectNode();
        ObjectNode institution = root.putObject("institution");
        institution.put("name", "SiPersurat");
        institution.put("shortName", "SiPersurat");
        institution.put("address", "");
        institution.put("city", "");
        institution.put("province", "");
        institution.put("postalCode", "");
        institution.put("phone", "");
        institution.put("email", "");
        institution.put("website", "");
        institution.put("headName", "");
        institution.put("headPosition", "");
        institution.put("logoName", "");
        institution.put("logoData", "");
        ObjectNode numbering = root.putObject("numbering");
        numbering.put("incomingPrefix", "SM");
        numbering.put("outgoingPrefix", "SK");
        numbering.put("dispositionPrefix", "DSP");
        numbering.put("archivePrefix", "ARS");
        numbering.put("activeYear", year);
        numbering.put("separator", "-");
        numbering.put("padding", 4);
        numbering.put("resetYearly", true);
        numbering.put("autoAgenda", true);
        ObjectNode agenda = root.putObject("agenda");
        agenda.put("dateFormat", "DD/MM/YYYY");
        agenda.put("timezone", "Asia/Makassar");
        agenda.put("workYear", year);
        agenda.put("incomingStartNumber", 1);
        agenda.put("outgoingStartNumber", 1);
        agenda.put("dispositionStartNumber", 1);
        ObjectNode notifications = root.putObject("notifications");
        notifications.put("incomingMail", true);
        notifications.put("disposition", true);
        notifications.put("outgoingApproval", true);
        notifications.put("outgoingApproved", true);
        notifications.put("dispositionDeadline", true);
        notifications.put("archive", false);
        notifications.put("system", true);
        notifications.put("deadlineReminderDays", 1);
        ObjectNode security = root.putObject("security");
        security.put("sessionMinutes", 120);
        security.put("autoLogout", true);
        security.put("minPasswordLength", 8);
        security.put("requireUppercase", true);
        security.put("requireLowercase", true);
        security.put("requireNumber", true);
        security.put("requireSymbol", false);
        security.put("maxLoginAttempts", 5);
        security.put("twoFactorEnabled", false);
        ObjectNode appearance = root.putObject("appearance");
        appearance.put("applicationName", "SiPersurat");
        appearance.put("sidebarName", "SiPersurat");
        appearance.put("footerText", "Sistem Informasi Persuratan");
        appearance.put("compactSidebar", false);
        appearance.put("showInstitutionName", true);
        root.put("updatedAt", "");
        return root;
    }

    private JsonNode parse(String value) {
        try { return objectMapper.readTree(value); }
        catch (JacksonException ex) { throw new BadRequestException("Data pengaturan pada database rusak"); }
    }

    private String stringify(JsonNode value) {
        try { return objectMapper.writeValueAsString(value); }
        catch (JacksonException ex) { throw new BadRequestException("Pengaturan gagal diproses"); }
    }

    private String required(JsonNode node, String field) {
        String value = node.path(field).asString("").trim();
        if (value.isBlank()) throw new BadRequestException("Backup tidak memiliki field " + field);
        return value;
    }

    private String safe(String value) { return value == null ? "" : value; }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }

    private <E extends Enum<E>> E enumValue(Class<E> type, String value, E fallback) {
        try { return Enum.valueOf(type, value); }
        catch (Exception ex) { return fallback; }
    }
}
