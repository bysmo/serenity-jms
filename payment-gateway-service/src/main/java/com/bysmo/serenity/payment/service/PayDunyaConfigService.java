package com.bysmo.serenity.payment.service;

import com.bysmo.serenity.common.exception.EntityNotFoundException;
import com.bysmo.serenity.payment.dto.PayDunyaConfigRequest;
import com.bysmo.serenity.payment.dto.PayDunyaConfigResponse;
import com.bysmo.serenity.payment.entity.PayDunyaConfiguration;
import com.bysmo.serenity.payment.repository.PayDunyaConfigurationRepository;
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
public class PayDunyaConfigService {

    private final PayDunyaConfigurationRepository payDunyaConfigurationRepository;

    @Transactional(readOnly = true)
    public List<PayDunyaConfigResponse> listConfigurations() {
        return payDunyaConfigurationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PayDunyaConfigResponse getConfiguration(UUID id) {
        PayDunyaConfiguration config = findConfigOrThrow(id);
        return toResponse(config);
    }

    @Transactional(readOnly = true)
    public PayDunyaConfigResponse getActiveConfiguration() {
        PayDunyaConfiguration config = payDunyaConfigurationRepository.findByIsActive(true)
                .orElseThrow(() -> new EntityNotFoundException("PayDunyaConfiguration", "active"));
        return toResponse(config);
    }

    public PayDunyaConfigResponse createConfiguration(PayDunyaConfigRequest request) {
        // Deactivate existing active configurations if this one is active
        if (request.getMode() == null || "test".equals(request.getMode()) || "live".equals(request.getMode())) {
            // Keep existing behavior - new config is active by default
            List<PayDunyaConfiguration> activeConfigs = payDunyaConfigurationRepository.findAll().stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                    .toList();
            for (PayDunyaConfiguration activeConfig : activeConfigs) {
                activeConfig.setIsActive(false);
                payDunyaConfigurationRepository.save(activeConfig);
            }
        }

        PayDunyaConfiguration config = PayDunyaConfiguration.builder()
                .id(UUID.randomUUID())
                .organisationId(request.getOrganisationId())
                .masterKey(request.getMasterKey())
                .privateKey(request.getPrivateKey())
                .publicKey(request.getPublicKey())
                .token(request.getToken())
                .mode(request.getMode() != null ? request.getMode() : "test")
                .ipnUrl(request.getIpnUrl())
                .isActive(true)
                .build();

        PayDunyaConfiguration saved = payDunyaConfigurationRepository.save(config);
        log.info("Created PayDunya configuration: id={}, mode={}", saved.getId(), saved.getMode());
        return toResponse(saved);
    }

    public PayDunyaConfigResponse updateConfiguration(UUID id, PayDunyaConfigRequest request) {
        PayDunyaConfiguration config = findConfigOrThrow(id);

        if (request.getOrganisationId() != null) {
            config.setOrganisationId(request.getOrganisationId());
        }
        if (request.getMasterKey() != null) {
            config.setMasterKey(request.getMasterKey());
        }
        if (request.getPrivateKey() != null) {
            config.setPrivateKey(request.getPrivateKey());
        }
        if (request.getPublicKey() != null) {
            config.setPublicKey(request.getPublicKey());
        }
        if (request.getToken() != null) {
            config.setToken(request.getToken());
        }
        if (request.getMode() != null) {
            config.setMode(request.getMode());
        }
        if (request.getIpnUrl() != null) {
            config.setIpnUrl(request.getIpnUrl());
        }

        PayDunyaConfiguration saved = payDunyaConfigurationRepository.save(config);
        log.info("Updated PayDunya configuration: id={}", saved.getId());
        return toResponse(saved);
    }

    public void deleteConfiguration(UUID id) {
        PayDunyaConfiguration config = findConfigOrThrow(id);
        payDunyaConfigurationRepository.delete(config);
        log.info("Deleted PayDunya configuration: id={}", id);
    }

    public PayDunyaConfigResponse activateConfiguration(UUID id) {
        // Deactivate all others
        List<PayDunyaConfiguration> activeConfigs = payDunyaConfigurationRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .toList();
        for (PayDunyaConfiguration activeConfig : activeConfigs) {
            activeConfig.setIsActive(false);
            payDunyaConfigurationRepository.save(activeConfig);
        }

        PayDunyaConfiguration config = findConfigOrThrow(id);
        config.setIsActive(true);
        PayDunyaConfiguration saved = payDunyaConfigurationRepository.save(config);
        log.info("Activated PayDunya configuration: id={}", id);
        return toResponse(saved);
    }

    private PayDunyaConfiguration findConfigOrThrow(UUID id) {
        return payDunyaConfigurationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PayDunyaConfiguration", id.toString()));
    }

    private PayDunyaConfigResponse toResponse(PayDunyaConfiguration config) {
        return PayDunyaConfigResponse.builder()
                .id(config.getId())
                .organisationId(config.getOrganisationId())
                .masterKey(config.getMasterKey())
                .privateKey(config.getPrivateKey())
                .publicKey(config.getPublicKey())
                .token(config.getToken())
                .mode(config.getMode())
                .ipnUrl(config.getIpnUrl())
                .isActive(config.getIsActive())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
