package com.serenity.admin.controller;

import com.serenity.admin.dto.AutoNumberingConfigRequest;
import com.serenity.admin.dto.AutoNumberingConfigResponse;
import com.serenity.admin.dto.NumberGenerationResponse;
import com.serenity.admin.service.AutoNumberingService;
import com.serenity.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/auto-numbering")
@RequiredArgsConstructor
public class AutoNumberingController {

    private final AutoNumberingService autoNumberingService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AutoNumberingConfigResponse>>> getAllConfigs() {
        log.debug("REST request to get all auto-numbering configs");
        List<AutoNumberingConfigResponse> configs = autoNumberingService.getAllConfigs();
        return ResponseEntity.ok(ApiResponse.success(configs));
    }

    @PostMapping("/generate/{objectType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<NumberGenerationResponse>> generateNumber(
            @PathVariable String objectType) {
        log.debug("REST request to generate number for object type: {}", objectType);
        NumberGenerationResponse response = autoNumberingService.generateNumber(objectType);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AutoNumberingConfigResponse>> createConfig(
            @Valid @RequestBody AutoNumberingConfigRequest request) {
        log.debug("REST request to create auto-numbering config: {}", request.getObjectType());
        AutoNumberingConfigResponse response = autoNumberingService.createConfig(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AutoNumberingConfigResponse>> toggleConfig(
            @PathVariable UUID id,
            @RequestParam boolean active) {
        log.debug("REST request to toggle auto-numbering config {}: active={}", id, active);
        AutoNumberingConfigResponse response = autoNumberingService.toggleConfig(id, active);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
