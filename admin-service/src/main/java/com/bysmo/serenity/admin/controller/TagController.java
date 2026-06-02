package com.bysmo.serenity.admin.controller;

import com.bysmo.serenity.admin.dto.TagRequest;
import com.bysmo.serenity.admin.dto.TagResponse;
import com.bysmo.serenity.admin.service.TagService;
import com.bysmo.serenity.common.dto.ApiResponse;
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
@RequestMapping("/api/v1/admin/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAll() {
        log.debug("REST request to get all tags");
        List<TagResponse> responses = tagService.getAll();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getByType(@PathVariable String type) {
        log.debug("REST request to get tags by type: {}", type);
        List<TagResponse> responses = tagService.getByType(type);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<TagResponse>> create(
            @Valid @RequestBody TagRequest request) {
        log.debug("REST request to create tag: {}", request.getNom());
        TagResponse response = tagService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<TagResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody TagRequest request) {
        log.debug("REST request to update tag: {}", id);
        TagResponse response = tagService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        log.debug("REST request to delete tag: {}", id);
        tagService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Tag deleted successfully", null));
    }
}
