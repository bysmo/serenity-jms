package com.bysmo.serenity.payment.repository;

import com.bysmo.serenity.payment.entity.PiSpiConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PiSpiConfigurationRepository extends JpaRepository<PiSpiConfiguration, UUID> {

    Optional<PiSpiConfiguration> findByOrganisationIdAndIsActive(UUID organisationId, Boolean isActive);

    Optional<PiSpiConfiguration> findByIsActive(Boolean isActive);
}
