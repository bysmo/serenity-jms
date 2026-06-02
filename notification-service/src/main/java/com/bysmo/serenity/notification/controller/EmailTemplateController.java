package com.bysmo.serenity.notification.controller;

import com.bysmo.serenity.notification.dto.EmailTemplateRequest;
import com.bysmo.serenity.notification.dto.EmailTemplateResponse;
import com.bysmo.serenity.notification.entity.EmailTemplate;
import com.bysmo.serenity.notification.repository.EmailTemplateRepository;
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
@RequestMapping("/api/v1/email-templates")
@RequiredArgsConstructor
@Tag(name = "Email Templates", description = "API de gestion des modèles d'email")
public class EmailTemplateController {

    private final EmailTemplateRepository emailTemplateRepository;

    @GetMapping
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    @Operation(summary = "Lister tous les modèles d'email")
    public ResponseEntity<List<EmailTemplateResponse>> getAll() {
        List<EmailTemplateResponse> responses = emailTemplateRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{nom}")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    @Operation(summary = "Récupérer un modèle d'email par son nom")
    public ResponseEntity<EmailTemplateResponse> getByNom(@PathVariable String nom) {
        EmailTemplate template = emailTemplateRepository.findByNom(nom)
                .orElseThrow(() -> new RuntimeException("Email template not found: " + nom));
        return ResponseEntity.ok(toResponse(template));
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    @Operation(summary = "Créer un modèle d'email")
    public ResponseEntity<EmailTemplateResponse> create(
            @Valid @RequestBody EmailTemplateRequest request) {
        EmailTemplate entity = toEntity(request);
        EmailTemplate saved = emailTemplateRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    @Operation(summary = "Mettre à jour un modèle d'email")
    public ResponseEntity<EmailTemplateResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody EmailTemplateRequest request) {
        EmailTemplate existing = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Email template not found: " + id));

        existing.setNom(request.getNom());
        existing.setSujet(request.getSujet());
        existing.setCorps(request.getCorps());
        existing.setType(request.getType());
        existing.setActif(request.getActif());

        EmailTemplate saved = emailTemplateRepository.save(existing);
        return ResponseEntity.ok(toResponse(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    @Operation(summary = "Supprimer un modèle d'email")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        emailTemplateRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private EmailTemplateResponse toResponse(EmailTemplate entity) {
        return EmailTemplateResponse.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .sujet(entity.getSujet())
                .corps(entity.getCorps())
                .type(entity.getType())
                .actif(entity.getActif())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private EmailTemplate toEntity(EmailTemplateRequest request) {
        return EmailTemplate.builder()
                .nom(request.getNom())
                .sujet(request.getSujet())
                .corps(request.getCorps())
                .type(request.getType())
                .actif(request.getActif())
                .build();
    }
}
