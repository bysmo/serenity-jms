package com.serenity.admin.service;

import com.serenity.admin.dto.EmailTemplateRequest;
import com.serenity.admin.dto.EmailTemplateResponse;
import com.serenity.admin.entity.EmailTemplate;
import com.serenity.admin.mapper.EmailTemplateMapper;
import com.serenity.admin.repository.EmailTemplateRepository;
import com.serenity.common.exception.DuplicateResourceException;
import com.serenity.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailTemplateMapper emailTemplateMapper;

    public List<EmailTemplateResponse> getAll() {
        log.debug("Fetching all email templates");
        List<EmailTemplate> templates = emailTemplateRepository.findAll();
        return emailTemplateMapper.toResponseList(templates);
    }

    public EmailTemplateResponse getByNom(String nom) {
        log.debug("Fetching email template by name: {}", nom);
        EmailTemplate template = emailTemplateRepository.findByNom(nom)
                .orElseThrow(() -> new EntityNotFoundException("EmailTemplate", nom));
        return emailTemplateMapper.toResponse(template);
    }

    public List<EmailTemplateResponse> getActiveByType(String type) {
        log.debug("Fetching active email templates by type: {}", type);
        List<EmailTemplate> templates = emailTemplateRepository.findByTypeAndActifTrue(type);
        return emailTemplateMapper.toResponseList(templates);
    }

    @Transactional
    public EmailTemplateResponse create(EmailTemplateRequest request) {
        log.info("Creating email template: {}", request.getNom());

        if (emailTemplateRepository.findByNom(request.getNom()).isPresent()) {
            throw new DuplicateResourceException(
                    "Email template with name '" + request.getNom() + "' already exists");
        }

        EmailTemplate entity = emailTemplateMapper.toEntity(request);
        if (entity.getActif() == null) {
            entity.setActif(true);
        }

        EmailTemplate saved = emailTemplateRepository.save(entity);
        log.info("Email template created with id: {}", saved.getId());
        return emailTemplateMapper.toResponse(saved);
    }

    @Transactional
    public EmailTemplateResponse update(UUID id, EmailTemplateRequest request) {
        log.info("Updating email template with id: {}", id);

        EmailTemplate existing = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("EmailTemplate", id));

        // Check name uniqueness if name is changing
        if (!existing.getNom().equals(request.getNom())) {
            if (emailTemplateRepository.findByNom(request.getNom()).isPresent()) {
                throw new DuplicateResourceException(
                        "Email template with name '" + request.getNom() + "' already exists");
            }
        }

        existing.setNom(request.getNom());
        existing.setSujet(request.getSujet());
        existing.setCorps(request.getCorps());
        existing.setType(request.getType());
        existing.setActif(request.getActif());

        EmailTemplate saved = emailTemplateRepository.save(existing);
        log.info("Email template updated with id: {}", saved.getId());
        return emailTemplateMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deleting email template with id: {}", id);
        if (!emailTemplateRepository.existsById(id)) {
            throw new EntityNotFoundException("EmailTemplate", id);
        }
        emailTemplateRepository.deleteById(id);
        log.info("Email template deleted with id: {}", id);
    }
}
