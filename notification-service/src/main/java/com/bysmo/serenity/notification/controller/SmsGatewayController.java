package com.bysmo.serenity.notification.controller;

import com.bysmo.serenity.notification.dto.SmsGatewayRequest;
import com.bysmo.serenity.notification.dto.SmsGatewayResponse;
import com.bysmo.serenity.notification.entity.SmsGateway;
import com.bysmo.serenity.notification.repository.SmsGatewayRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications/sms-gateways")
@RequiredArgsConstructor
@Tag(name = "SMS Gateways", description = "API de gestion des passerelles SMS")
public class SmsGatewayController {

    private final SmsGatewayRepository smsGatewayRepository;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Lister toutes les passerelles SMS")
    public ResponseEntity<List<SmsGatewayResponse>> getAll() {
        List<SmsGatewayResponse> responses = smsGatewayRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Créer une passerelle SMS")
    public ResponseEntity<SmsGatewayResponse> create(
            @Valid @RequestBody SmsGatewayRequest request) {
        SmsGateway entity = toEntity(request);
        SmsGateway saved = smsGatewayRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Mettre à jour une passerelle SMS")
    public ResponseEntity<SmsGatewayResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody SmsGatewayRequest request) {
        SmsGateway existing = smsGatewayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SMS gateway not found: " + id));

        existing.setNom(request.getNom());
        existing.setProviderCode(request.getProviderCode());
        existing.setApiUrl(request.getApiUrl());
        existing.setApiKey(request.getApiKey());
        existing.setSenderName(request.getSenderName());
        existing.setIsActive(request.getIsActive());
        existing.setOrdre(request.getOrdre());
        existing.setMaxRetries(request.getMaxRetries());
        existing.setTimeoutSeconds(request.getTimeoutSeconds());

        SmsGateway saved = smsGatewayRepository.save(existing);
        return ResponseEntity.ok(toResponse(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Supprimer une passerelle SMS")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        smsGatewayRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private SmsGatewayResponse toResponse(SmsGateway entity) {
        return SmsGatewayResponse.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .providerCode(entity.getProviderCode())
                .apiUrl(entity.getApiUrl())
                .apiKey(entity.getApiKey())
                .senderName(entity.getSenderName())
                .isActive(entity.getIsActive())
                .ordre(entity.getOrdre())
                .maxRetries(entity.getMaxRetries())
                .timeoutSeconds(entity.getTimeoutSeconds())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private SmsGateway toEntity(SmsGatewayRequest request) {
        return SmsGateway.builder()
                .nom(request.getNom())
                .providerCode(request.getProviderCode())
                .apiUrl(request.getApiUrl())
                .apiKey(request.getApiKey())
                .senderName(request.getSenderName())
                .isActive(request.getIsActive())
                .ordre(request.getOrdre())
                .maxRetries(request.getMaxRetries())
                .timeoutSeconds(request.getTimeoutSeconds())
                .build();
    }
}
