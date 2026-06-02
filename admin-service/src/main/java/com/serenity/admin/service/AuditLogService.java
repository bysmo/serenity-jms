package com.serenity.admin.service;

import com.serenity.admin.dto.AuditLogResponse;
import com.serenity.admin.entity.AuditLog;
import com.serenity.admin.entity.enums.ActorType;
import com.serenity.admin.mapper.AuditLogMapper;
import com.serenity.admin.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Transactional
    public void log(ActorType actorType, UUID actorId, String action, String model,
                    UUID modelId, String oldValues, String newValues,
                    String ipAddress, String userAgent) {
        log.info("Logging audit: actorType={}, actorId={}, action={}, model={}, modelId={}",
                actorType, actorId, action, model, modelId);

        AuditLog auditLog = AuditLog.builder()
                .actorType(actorType)
                .actorId(actorId)
                .action(action)
                .model(model)
                .modelId(modelId)
                .oldValues(oldValues)
                .newValues(newValues)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        auditLogRepository.save(auditLog);
        log.debug("Audit log saved successfully");
    }

    public Page<AuditLogResponse> getAll(Pageable pageable) {
        log.debug("Fetching all audit logs - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return auditLogRepository.findAll(pageable).map(auditLogMapper::toResponse);
    }

    public List<AuditLogResponse> getByActor(ActorType actorType, UUID actorId) {
        log.debug("Fetching audit logs by actor: type={}, id={}", actorType, actorId);
        List<AuditLog> logs = auditLogRepository.findByActorTypeAndActorId(actorType, actorId);
        return auditLogMapper.toResponseList(logs);
    }

    public List<AuditLogResponse> getByModel(String model, UUID modelId) {
        log.debug("Fetching audit logs by model: {}, id={}", model, modelId);
        List<AuditLog> logs = auditLogRepository.findByModelAndModelId(model, modelId);
        return auditLogMapper.toResponseList(logs);
    }

    public List<AuditLogResponse> getByDateRange(LocalDateTime start, LocalDateTime end) {
        log.debug("Fetching audit logs between {} and {}", start, end);
        List<AuditLog> logs = auditLogRepository.findByCreatedAtBetween(start, end);
        return auditLogMapper.toResponseList(logs);
    }
}
