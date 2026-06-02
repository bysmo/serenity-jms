package com.serenity.member.controller;

import com.serenity.member.dto.MembreDashboardResponse;
import com.serenity.member.dto.MembreRegistrationRequest;
import com.serenity.member.dto.MembreRequest;
import com.serenity.member.dto.MembreResponse;
import com.serenity.member.service.MembreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Member management APIs")
public class MembreController {

    private final MembreService membreService;

    @GetMapping
    @PreAuthorize("hasRole('GESTION_MEMBRES')")
    @Operation(summary = "Get all members (paginated)")
    public ResponseEntity<Page<MembreResponse>> getAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.debug("GET /api/v1/members");
        return ResponseEntity.ok(membreService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID")
    public ResponseEntity<MembreResponse> getById(@PathVariable UUID id) {
        log.debug("GET /api/v1/members/{}", id);
        return ResponseEntity.ok(membreService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTION_MEMBRES')")
    @Operation(summary = "Create a new member")
    public ResponseEntity<MembreResponse> create(@Valid @RequestBody MembreRequest request) {
        log.info("POST /api/v1/members");
        return ResponseEntity.status(HttpStatus.CREATED).body(membreService.create(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Mobile registration")
    public ResponseEntity<MembreResponse> register(@Valid @RequestBody MembreRegistrationRequest request) {
        log.info("POST /api/v1/members/register");
        return ResponseEntity.status(HttpStatus.CREATED).body(membreService.register(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTION_MEMBRES')")
    @Operation(summary = "Update member")
    public ResponseEntity<MembreResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody MembreRequest request) {
        log.info("PUT /api/v1/members/{}", id);
        return ResponseEntity.ok(membreService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete member")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/members/{}", id);
        membreService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dashboard")
    @Operation(summary = "Get member dashboard data")
    public ResponseEntity<MembreDashboardResponse> getDashboard(@PathVariable UUID id) {
        log.debug("GET /api/v1/members/{}/dashboard", id);
        return ResponseEntity.ok(membreService.updateDashboard(id));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "OTP verification placeholder")
    public ResponseEntity<Void> verifyOtp() {
        log.debug("POST /api/v1/members/verify-otp - placeholder");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search member by telephone")
    public ResponseEntity<MembreResponse> searchByTelephone(@RequestParam String telephone) {
        log.debug("GET /api/v1/members/search?telephone={}", telephone);
        return ResponseEntity.ok(membreService.getByTelephone(telephone));
    }
}
