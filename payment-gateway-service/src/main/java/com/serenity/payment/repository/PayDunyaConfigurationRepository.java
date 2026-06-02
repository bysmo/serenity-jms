package com.serenity.payment.repository;

import com.serenity.payment.entity.PayDunyaConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PayDunyaConfigurationRepository extends JpaRepository<PayDunyaConfiguration, UUID> {

    Optional<PayDunyaConfiguration> findByOrganisationIdAndIsActive(UUID organisationId, Boolean isActive);

    Optional<PayDunyaConfiguration> findByIsActive(Boolean isActive);
}
