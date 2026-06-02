package com.serenity.payment.service;

import com.serenity.common.exception.EntityNotFoundException;
import com.serenity.payment.dto.PiSpiConfigRequest;
import com.serenity.payment.dto.PiSpiConfigResponse;
import com.serenity.payment.entity.PiSpiConfiguration;
import com.serenity.payment.repository.PiSpiConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PiSpiConfigService {

    private final PiSpiConfigurationRepository piSpiConfigurationRepository;

    @Transactional(readOnly = true)
    public List<PiSpiConfigResponse> listConfigurations() {
        return piSpiConfigurationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PiSpiConfigResponse getConfiguration(UUID id) {
        PiSpiConfiguration config = findConfigOrThrow(id);
        return toResponse(config);
    }

    @Transactional(readOnly = true)
    public PiSpiConfigResponse getActiveConfiguration() {
        PiSpiConfiguration config = piSpiConfigurationRepository.findByIsActive(true)
                .orElseThrow(() -> new EntityNotFoundException("PiSpiConfiguration", "active"));
        return toResponse(config);
    }

    public PiSpiConfigResponse createConfiguration(PiSpiConfigRequest request) {
        // Deactivate existing active configurations
        List<PiSpiConfiguration> activeConfigs = piSpiConfigurationRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .toList();
        for (PiSpiConfiguration activeConfig : activeConfigs) {
            activeConfig.setIsActive(false);
            piSpiConfigurationRepository.save(activeConfig);
        }

        PiSpiConfiguration config = PiSpiConfiguration.builder()
                .id(UUID.randomUUID())
                .organisationId(request.getOrganisationId())
                .clientId(request.getClientId())
                .clientSecret(request.getClientSecret())
                .apiKey(request.getApiKey())
                .payeAlias(request.getPayeAlias())
                .mode(request.getMode() != null ? request.getMode() : "test")
                .callbackUrl(request.getCallbackUrl())
                .isActive(true)
                .build();

        PiSpiConfiguration saved = piSpiConfigurationRepository.save(config);
        log.info("Created Pi-SPI configuration: id={}, mode={}", saved.getId(), saved.getMode());
        return toResponse(saved);
    }

    public PiSpiConfigResponse updateConfiguration(UUID id, PiSpiConfigRequest request) {
        PiSpiConfiguration config = findConfigOrThrow(id);

        if (request.getOrganisationId() != null) {
            config.setOrganisationId(request.getOrganisationId());
        }
        if (request.getClientId() != null) {
            config.setClientId(request.getClientId());
        }
        if (request.getClientSecret() != null) {
            config.setClientSecret(request.getClientSecret());
        }
        if (request.getApiKey() != null) {
            config.setApiKey(request.getApiKey());
        }
        if (request.getPayeAlias() != null) {
            config.setPayeAlias(request.getPayeAlias());
        }
        if (request.getMode() != null) {
            config.setMode(request.getMode());
        }
        if (request.getCallbackUrl() != null) {
            config.setCallbackUrl(request.getCallbackUrl());
        }

        PiSpiConfiguration saved = piSpiConfigurationRepository.save(config);
        log.info("Updated Pi-SPI configuration: id={}", saved.getId());
        return toResponse(saved);
    }

    public void deleteConfiguration(UUID id) {
        PiSpiConfiguration config = findConfigOrThrow(id);
        piSpiConfigurationRepository.delete(config);
        log.info("Deleted Pi-SPI configuration: id={}", id);
    }

    public PiSpiConfigResponse activateConfiguration(UUID id) {
        // Deactivate all others
        List<PiSpiConfiguration> activeConfigs = piSpiConfigurationRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .toList();
        for (PiSpiConfiguration activeConfig : activeConfigs) {
            activeConfig.setIsActive(false);
            piSpiConfigurationRepository.save(activeConfig);
        }

        PiSpiConfiguration config = findConfigOrThrow(id);
        config.setIsActive(true);
        PiSpiConfiguration saved = piSpiConfigurationRepository.save(config);
        log.info("Activated Pi-SPI configuration: id={}", id);
        return toResponse(saved);
    }

    private PiSpiConfiguration findConfigOrThrow(UUID id) {
        return piSpiConfigurationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PiSpiConfiguration", id.toString()));
    }

    private PiSpiConfigResponse toResponse(PiSpiConfiguration config) {
        return PiSpiConfigResponse.builder()
                .id(config.getId())
                .organisationId(config.getOrganisationId())
                .clientId(config.getClientId())
                .clientSecret(config.getClientSecret())
                .apiKey(config.getApiKey())
                .payeAlias(config.getPayeAlias())
                .mode(config.getMode())
                .callbackUrl(config.getCallbackUrl())
                .isActive(config.getIsActive())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
