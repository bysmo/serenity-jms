package com.bysmo.serenity.member.controller;

import com.bysmo.serenity.member.dto.SegmentRequest;
import com.bysmo.serenity.member.dto.SegmentResponse;
import com.bysmo.serenity.member.service.SegmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/segments")
@RequiredArgsConstructor
@Tag(name = "Segments", description = "Segment management APIs")
public class SegmentController {

    private final SegmentService segmentService;

    @GetMapping
    @Operation(summary = "Get all segments")
    public ResponseEntity<List<SegmentResponse>> getAll() {
        log.debug("GET /api/v1/segments");
        return ResponseEntity.ok(segmentService.getAll());
    }

    @PostMapping
    @Operation(summary = "Create a new segment")
    public ResponseEntity<SegmentResponse> create(@Valid @RequestBody SegmentRequest request) {
        log.info("POST /api/v1/segments");
        return ResponseEntity.status(HttpStatus.CREATED).body(segmentService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a segment")
    public ResponseEntity<SegmentResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody SegmentRequest request) {
        log.info("PUT /api/v1/segments/{}", id);
        return ResponseEntity.ok(segmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a segment")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/segments/{}", id);
        segmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
