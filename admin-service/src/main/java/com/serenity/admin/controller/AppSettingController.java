package com.serenity.admin.controller;

import com.serenity.admin.dto.AppSettingRequest;
import com.serenity.admin.dto.AppSettingResponse;
import com.serenity.admin.service.AppSettingService;
import com.serenity.common.dto.ApiResponse;
import com.serenity.common.dto.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class AppSettingController {

    private final AppSettingService appSettingService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AppSettingResponse>>> getAll(Pageable pageable) {
        log.debug("REST request to get all app settings");
        Page<AppSettingResponse> page = appSettingService.getAll(pageable);
        PagedResponse<AppSettingResponse> pagedResponse = PagedResponse.of(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{cle}")
    public ResponseEntity<ApiResponse<AppSettingResponse>> getByCle(@PathVariable String cle) {
        log.debug("REST request to get app setting by key: {}", cle);
        AppSettingResponse response = appSettingService.getByCle(cle);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/group/{groupe}")
    public ResponseEntity<ApiResponse<List<AppSettingResponse>>> getByGroupe(@PathVariable String groupe) {
        log.debug("REST request to get app settings by group: {}", groupe);
        List<AppSettingResponse> responses = appSettingService.getByGroupe(groupe);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<AppSettingResponse>> create(
            @Valid @RequestBody AppSettingRequest request) {
        log.debug("REST request to create app setting: {}", request.getCle());
        AppSettingResponse response = appSettingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<AppSettingResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody AppSettingRequest request) {
        log.debug("REST request to update app setting: {}", id);
        AppSettingResponse response = appSettingService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        log.debug("REST request to delete app setting: {}", id);
        appSettingService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Setting deleted successfully", null));
    }
}
