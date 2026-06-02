package com.serenity.member.repository;

import com.serenity.member.entity.ParrainageConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParrainageConfigRepository extends JpaRepository<ParrainageConfig, UUID> {

    Optional<ParrainageConfig> findTopByOrderByCreatedAtDesc();
}
