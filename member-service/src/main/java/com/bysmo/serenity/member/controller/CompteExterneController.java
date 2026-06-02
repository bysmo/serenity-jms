package com.bysmo.serenity.member.controller;

import com.bysmo.serenity.member.dto.CompteExterneRequest;
import com.bysmo.serenity.member.dto.CompteExterneResponse;
import com.bysmo.serenity.member.service.CompteExterneService;
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
@RequestMapping("/api/v1/comptes-externes")
@RequiredArgsConstructor
@Tag(name = "Comptes Externes", description = "External accounts management APIs")
public class CompteExterneController {

    private final CompteExterneService compteExterneService;

    @GetMapping("/membre/{membreId}")
    @Operation(summary = "Get external accounts for a member")
    public ResponseEntity<List<CompteExterneResponse>> getByMembreId(@PathVariable UUID membreId) {
        log.debug("GET /api/v1/comptes-externes/membre/{}", membreId);
        return ResponseEntity.ok(compteExterneService.getByMembreId(membreId));
    }

    @PostMapping("/membre/{membreId}")
    @Operation(summary = "Create an external account for a member")
    public ResponseEntity<CompteExterneResponse> create(
            @PathVariable UUID membreId,
            @Valid @RequestBody CompteExterneRequest request) {
        log.info("POST /api/v1/comptes-externes/membre/{}", membreId);
        return ResponseEntity.status(HttpStatus.CREATED).body(compteExterneService.create(membreId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an external account")
    public ResponseEntity<CompteExterneResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CompteExterneRequest request) {
        log.info("PUT /api/v1/comptes-externes/{}", id);
        return ResponseEntity.ok(compteExterneService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an external account")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/comptes-externes/{}", id);
        compteExterneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
