package com.bysmo.serenity.admin.repository;

import com.bysmo.serenity.admin.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, UUID> {

    Optional<EmailTemplate> findByNom(String nom);

    List<EmailTemplate> findByTypeAndActifTrue(String type);
}
