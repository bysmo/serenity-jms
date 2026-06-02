package com.serenity.admin.service;

import com.serenity.admin.dto.AutoNumberingConfigRequest;
import com.serenity.admin.dto.AutoNumberingConfigResponse;
import com.serenity.admin.dto.NumberGenerationResponse;
import com.serenity.admin.entity.AutoNumberingConfig;
import com.serenity.admin.mapper.AutoNumberingConfigMapper;
import com.serenity.admin.repository.AutoNumberingConfigRepository;
import com.serenity.common.exception.DuplicateResourceException;
import com.serenity.common.exception.EntityNotFoundException;
import com.serenity.common.util.AutoNumberingUtil;
import com.serenity.common.util.ChecksumUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoNumberingService {

    private final AutoNumberingConfigRepository autoNumberingConfigRepository;
    private final AutoNumberingConfigMapper autoNumberingConfigMapper;
    private final ObjectMapper objectMapper;

    /**
     * Generate the next number for a given object type.
     * This method is transactional to ensure atomic increment.
     * Format: PREFIX-000001 (e.g., MBR-000001)
     */
    @Transactional
    public NumberGenerationResponse generateNumber(String objectType) {
        log.info("Generating number for object type: {}", objectType);

        AutoNumberingConfig config = autoNumberingConfigRepository.findByObjectType(objectType)
                .orElseThrow(() -> new EntityNotFoundException("AutoNumberingConfig", objectType));

        if (!Boolean.TRUE.equals(config.getIsActive())) {
            throw new IllegalStateException("Auto numbering is not active for object type: " + objectType);
        }

        Long currentValue = config.getCurrentValue();
        Long nextValue = currentValue + 1;
        config.setCurrentValue(nextValue);

        // Extract prefix from definition JSON
        String prefix = extractPrefix(config.getDefinition());

        String generatedNumber = AutoNumberingUtil.generate(prefix, nextValue);
        config.setChecksum(computeChecksum(config));

        autoNumberingConfigRepository.save(config);

        log.info("Generated number for {}: {} (sequence: {})", objectType, generatedNumber, nextValue);

        return NumberGenerationResponse.builder()
                .prefix(prefix)
                .sequence(nextValue)
                .generatedNumber(generatedNumber)
                .build();
    }

    public List<AutoNumberingConfigResponse> getActiveConfigs() {
        log.debug("Fetching active auto-numbering configs");
        List<AutoNumberingConfig> configs = autoNumberingConfigRepository.findByIsActiveTrue();
        return autoNumberingConfigMapper.toResponseList(configs);
    }

    public List<AutoNumberingConfigResponse> getAllConfigs() {
        log.debug("Fetching all auto-numbering configs");
        List<AutoNumberingConfig> configs = autoNumberingConfigRepository.findAll();
        return autoNumberingConfigMapper.toResponseList(configs);
    }

    @Transactional
    public AutoNumberingConfigResponse createConfig(AutoNumberingConfigRequest request) {
        log.info("Creating auto-numbering config for object type: {}", request.getObjectType());

        if (autoNumberingConfigRepository.findByObjectType(request.getObjectType()).isPresent()) {
            throw new DuplicateResourceException(
                    "Auto numbering config for object type '" + request.getObjectType() + "' already exists");
        }

        AutoNumberingConfig entity = autoNumberingConfigMapper.toEntity(request);
        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }
        entity.setCurrentValue(0L);
        entity.setChecksum(computeChecksum(entity));

        AutoNumberingConfig saved = autoNumberingConfigRepository.save(entity);
        log.info("Auto-numbering config created with id: {}", saved.getId());
        return autoNumberingConfigMapper.toResponse(saved);
    }

    @Transactional
    public AutoNumberingConfigResponse toggleConfig(UUID id, boolean active) {
        log.info("Toggling auto-numbering config id: {} to active: {}", id, active);

        AutoNumberingConfig config = autoNumberingConfigRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AutoNumberingConfig", id));

        config.setIsActive(active);
        config.setChecksum(computeChecksum(config));

        AutoNumberingConfig saved = autoNumberingConfigRepository.save(config);
        log.info("Auto-numbering config {} toggled to active: {}", id, active);
        return autoNumberingConfigMapper.toResponse(saved);
    }

    /**
     * Initialize checksums for all configs that don't have one.
     * Manual trigger endpoint.
     */
    @Transactional
    public void initializeChecksums() {
        log.info("Initializing checksums for all auto-numbering configs");

        List<AutoNumberingConfig> configs = autoNumberingConfigRepository.findAll();
        int updated = 0;

        for (AutoNumberingConfig config : configs) {
            String newChecksum = computeChecksum(config);
            if (config.getChecksum() == null || !config.getChecksum().equals(newChecksum)) {
                config.setChecksum(newChecksum);
                autoNumberingConfigRepository.save(config);
                updated++;
            }
        }

        log.info("Checksums initialized: {} configs updated out of {}", updated, configs.size());
    }

    private String extractPrefix(String definition) {
        try {
            JsonNode node = objectMapper.readTree(definition);
            if (node.has("prefix")) {
                return node.get("prefix").asText();
            }
        } catch (Exception e) {
            log.error("Failed to parse definition JSON: {}", definition, e);
        }
        return "GEN";
    }

    private String computeChecksum(AutoNumberingConfig config) {
        String data = config.getObjectType() + "|" + config.getDefinition() + "|"
                + config.getCurrentValue() + "|" + config.getIsActive();
        return ChecksumUtil.sha256(data);
    }
}
