package com.serenity.notification.repository;

import com.serenity.notification.entity.SmtpConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SmtpConfigurationRepository extends JpaRepository<SmtpConfiguration, UUID> {

    List<SmtpConfiguration> findByActifTrue();
}
