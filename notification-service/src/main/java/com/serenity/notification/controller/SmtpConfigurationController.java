package com.serenity.notification.controller;

import com.serenity.notification.dto.SmtpConfigurationRequest;
import com.serenity.notification.dto.SmtpConfigurationResponse;
import com.serenity.notification.entity.SmtpConfiguration;
import com.serenity.notification.repository.SmtpConfigurationRepository;
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
@RequestMapping("/api/v1/notifications/smtp")
@RequiredArgsConstructor
@Tag(name = "SMTP Configuration", description = "API de gestion des configurations SMTP")
public class SmtpConfigurationController {

    private final SmtpConfigurationRepository smtpConfigurationRepository;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Lister toutes les configurations SMTP")
    public ResponseEntity<List<SmtpConfigurationResponse>> getAll() {
        List<SmtpConfigurationResponse> responses = smtpConfigurationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Créer une configuration SMTP")
    public ResponseEntity<SmtpConfigurationResponse> create(
            @Valid @RequestBody SmtpConfigurationRequest request) {
        SmtpConfiguration entity = toEntity(request);
        SmtpConfiguration saved = smtpConfigurationRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Mettre à jour une configuration SMTP")
    public ResponseEntity<SmtpConfigurationResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody SmtpConfigurationRequest request) {
        SmtpConfiguration existing = smtpConfigurationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SMTP configuration not found: " + id));

        existing.setHost(request.getHost());
        existing.setPort(request.getPort());
        existing.setUsername(request.getUsername());
        existing.setPassword(request.getPassword());
        existing.setAuthEnabled(request.getAuthEnabled());
        existing.setStarttlsEnabled(request.getStarttlsEnabled());
        existing.setSslEnabled(request.getSslEnabled());
        existing.setFromEmail(request.getFromEmail());
        existing.setFromName(request.getFromName());
        existing.setActif(request.getActif());

        SmtpConfiguration saved = smtpConfigurationRepository.save(existing);
        return ResponseEntity.ok(toResponse(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Supprimer une configuration SMTP")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        smtpConfigurationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private SmtpConfigurationResponse toResponse(SmtpConfiguration entity) {
        return SmtpConfigurationResponse.builder()
                .id(entity.getId())
                .host(entity.getHost())
                .port(entity.getPort())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .authEnabled(entity.getAuthEnabled())
                .starttlsEnabled(entity.getStarttlsEnabled())
                .sslEnabled(entity.getSslEnabled())
                .fromEmail(entity.getFromEmail())
                .fromName(entity.getFromName())
                .actif(entity.getActif())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private SmtpConfiguration toEntity(SmtpConfigurationRequest request) {
        return SmtpConfiguration.builder()
                .host(request.getHost())
                .port(request.getPort())
                .username(request.getUsername())
                .password(request.getPassword())
                .authEnabled(request.getAuthEnabled())
                .starttlsEnabled(request.getStarttlsEnabled())
                .sslEnabled(request.getSslEnabled())
                .fromEmail(request.getFromEmail())
                .fromName(request.getFromName())
                .actif(request.getActif())
                .build();
    }
}
