package com.serenity.payment.controller;

import com.serenity.common.dto.ApiResponse;
import com.serenity.payment.dto.PiSpiConfigRequest;
import com.serenity.payment.dto.PiSpiConfigResponse;
import com.serenity.payment.service.PiSpiConfigService;
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
@RequestMapping("/api/v1/payment-gateways/pispi")
@RequiredArgsConstructor
public class PiSpiConfigController {

    private final PiSpiConfigService piSpiConfigService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<PiSpiConfigResponse>>> listConfigurations() {
        List<PiSpiConfigResponse> configs = piSpiConfigService.listConfigurations();
        return ResponseEntity.ok(ApiResponse.success(configs));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PiSpiConfigResponse>> getConfiguration(@PathVariable UUID id) {
        PiSpiConfigResponse config = piSpiConfigService.getConfiguration(id);
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PiSpiConfigResponse>> getActiveConfiguration() {
        PiSpiConfigResponse config = piSpiConfigService.getActiveConfiguration();
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PiSpiConfigResponse>> createConfiguration(
            @Valid @RequestBody PiSpiConfigRequest request) {
        PiSpiConfigResponse config = piSpiConfigService.createConfiguration(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Pi-SPI configuration created successfully", config));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PiSpiConfigResponse>> updateConfiguration(
            @PathVariable UUID id,
            @Valid @RequestBody PiSpiConfigRequest request) {
        PiSpiConfigResponse config = piSpiConfigService.updateConfiguration(id, request);
        return ResponseEntity.ok(ApiResponse.success("Pi-SPI configuration updated successfully", config));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PiSpiConfigResponse>> activateConfiguration(@PathVariable UUID id) {
        PiSpiConfigResponse config = piSpiConfigService.activateConfiguration(id);
        return ResponseEntity.ok(ApiResponse.success("Pi-SPI configuration activated successfully", config));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteConfiguration(@PathVariable UUID id) {
        piSpiConfigService.deleteConfiguration(id);
        return ResponseEntity.ok(ApiResponse.success("Pi-SPI configuration deleted successfully", null));
    }
}
