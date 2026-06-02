package com.bysmo.serenity.notification.repository;

import com.bysmo.serenity.notification.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, UUID> {

    Optional<EmailTemplate> findByNomAndActifTrue(String nom);

    Optional<EmailTemplate> findByNom(String nom);
}
