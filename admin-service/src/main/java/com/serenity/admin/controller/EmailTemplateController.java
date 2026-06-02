package com.serenity.admin.controller;

import com.serenity.admin.dto.EmailTemplateRequest;
import com.serenity.admin.dto.EmailTemplateResponse;
import com.serenity.admin.service.EmailTemplateService;
import com.serenity.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1/email-templates")
@RequiredArgsConstructor
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmailTemplateResponse>>> getAll() {
        log.debug("REST request to get all email templates");
        List<EmailTemplateResponse> responses = emailTemplateService.getAll();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> create(
            @Valid @RequestBody EmailTemplateRequest request) {
        log.debug("REST request to create email template: {}", request.getNom());
        EmailTemplateResponse response = emailTemplateService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<EmailTemplateResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody EmailTemplateRequest request) {
        log.debug("REST request to update email template: {}", id);
        EmailTemplateResponse response = emailTemplateService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        log.debug("REST request to delete email template: {}", id);
        emailTemplateService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Email template deleted successfully", null));
    }
}
