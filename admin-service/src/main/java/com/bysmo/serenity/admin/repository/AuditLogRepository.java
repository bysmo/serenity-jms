package com.bysmo.serenity.admin.repository;

import com.bysmo.serenity.admin.entity.AuditLog;
import com.bysmo.serenity.admin.entity.enums.ActorType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByActorTypeAndActorId(ActorType actorType, UUID actorId);

    List<AuditLog> findByModelAndModelId(String model, UUID modelId);

    List<AuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
