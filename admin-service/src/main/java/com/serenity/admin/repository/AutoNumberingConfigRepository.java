package com.serenity.admin.repository;

import com.serenity.admin.entity.AutoNumberingConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AutoNumberingConfigRepository extends JpaRepository<AutoNumberingConfig, UUID> {

    Optional<AutoNumberingConfig> findByObjectType(String objectType);

    List<AutoNumberingConfig> findByIsActiveTrue();
}
