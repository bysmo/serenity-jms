package com.bysmo.serenity.admin.controller;

import com.bysmo.serenity.admin.dto.AuditLogResponse;
import com.bysmo.serenity.admin.entity.enums.ActorType;
import com.bysmo.serenity.admin.service.AuditFinancierService;
import com.bysmo.serenity.admin.service.AuditLogService;
import com.bysmo.serenity.common.dto.ApiResponse;
import com.bysmo.serenity.common.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final AuditFinancierService auditFinancierService;

    @GetMapping
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<AuditLogResponse>>> getAll(
            Pageable pageable,
            @RequestParam(required = false) String actorType,
            @RequestParam(required = false) UUID actorId,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) UUID modelId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        log.debug("REST request to get audit logs with filters");

        if (actorType != null && actorId != null) {
            List<AuditLogResponse> logs = auditLogService.getByActor(ActorType.valueOf(actorType.toUpperCase()), actorId);
            return ResponseEntity.ok(ApiResponse.success(PagedResponse.of(logs, 0, logs.size(), logs.size(), 1, true)));
        }

        if (model != null && modelId != null) {
            List<AuditLogResponse> logs = auditLogService.getByModel(model, modelId);
            return ResponseEntity.ok(ApiResponse.success(PagedResponse.of(logs, 0, logs.size(), logs.size(), 1, true)));
        }

        if (startDate != null && endDate != null) {
            List<AuditLogResponse> logs = auditLogService.getByDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(PagedResponse.of(logs, 0, logs.size(), logs.size(), 1, true)));
        }

        Page<AuditLogResponse> page = auditLogService.getAll(pageable);
        PagedResponse<AuditLogResponse> pagedResponse = PagedResponse.of(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/verify/{tableName}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> verifyChain(@PathVariable String tableName) {
        log.debug("REST request to verify Merkle chain for table: {}", tableName);
        boolean isValid = auditFinancierService.verifyChain(tableName);
        return ResponseEntity.ok(ApiResponse.success(
                isValid ? "Chain integrity verified" : "Chain integrity BROKEN", isValid));
    }
}
