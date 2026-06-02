package com.serenity.admin.controller;

import com.serenity.admin.dto.AnnonceRequest;
import com.serenity.admin.dto.AnnonceResponse;
import com.serenity.admin.service.AnnonceService;
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
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
public class AnnonceController {

    private final AnnonceService annonceService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AnnonceResponse>>> getAll(Pageable pageable) {
        log.debug("REST request to get all annonces");
        Page<AnnonceResponse> page = annonceService.getAll(pageable);
        PagedResponse<AnnonceResponse> pagedResponse = PagedResponse.of(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<AnnonceResponse>>> getActive() {
        log.debug("REST request to get active annonces");
        List<AnnonceResponse> responses = annonceService.getActive();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<AnnonceResponse>> create(
            @Valid @RequestBody AnnonceRequest request) {
        log.debug("REST request to create annonce: {}", request.getTitre());
        AnnonceResponse response = annonceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<AnnonceResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody AnnonceRequest request) {
        log.debug("REST request to update annonce: {}", id);
        AnnonceResponse response = annonceService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        log.debug("REST request to delete annonce: {}", id);
        annonceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted successfully", null));
    }
}
