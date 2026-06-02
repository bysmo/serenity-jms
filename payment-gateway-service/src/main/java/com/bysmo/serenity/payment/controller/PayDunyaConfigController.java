package com.bysmo.serenity.payment.controller;

import com.bysmo.serenity.common.dto.ApiResponse;
import com.bysmo.serenity.payment.dto.PayDunyaConfigRequest;
import com.bysmo.serenity.payment.dto.PayDunyaConfigResponse;
import com.bysmo.serenity.payment.service.PayDunyaConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment-gateways/paydunya")
@RequiredArgsConstructor
public class PayDunyaConfigController {

    private final PayDunyaConfigService payDunyaConfigService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<PayDunyaConfigResponse>>> listConfigurations() {
        List<PayDunyaConfigResponse> configs = payDunyaConfigService.listConfigurations();
        return ResponseEntity.ok(ApiResponse.success(configs));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PayDunyaConfigResponse>> getConfiguration(@PathVariable UUID id) {
        PayDunyaConfigResponse config = payDunyaConfigService.getConfiguration(id);
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PayDunyaConfigResponse>> getActiveConfiguration() {
        PayDunyaConfigResponse config = payDunyaConfigService.getActiveConfiguration();
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PayDunyaConfigResponse>> createConfiguration(
            @Valid @RequestBody PayDunyaConfigRequest request) {
        PayDunyaConfigResponse config = payDunyaConfigService.createConfiguration(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("PayDunya configuration created successfully", config));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PayDunyaConfigResponse>> updateConfiguration(
            @PathVariable UUID id,
            @Valid @RequestBody PayDunyaConfigRequest request) {
        PayDunyaConfigResponse config = payDunyaConfigService.updateConfiguration(id, request);
        return ResponseEntity.ok(ApiResponse.success("PayDunya configuration updated successfully", config));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PayDunyaConfigResponse>> activateConfiguration(@PathVariable UUID id) {
        PayDunyaConfigResponse config = payDunyaConfigService.activateConfiguration(id);
        return ResponseEntity.ok(ApiResponse.success("PayDunya configuration activated successfully", config));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteConfiguration(@PathVariable UUID id) {
        payDunyaConfigService.deleteConfiguration(id);
        return ResponseEntity.ok(ApiResponse.success("PayDunya configuration deleted successfully", null));
    }
}
