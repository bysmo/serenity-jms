package com.bysmo.serenity.member.controller;

import com.bysmo.serenity.member.dto.PinEnableRequest;
import com.bysmo.serenity.member.dto.PinSetupRequest;
import com.bysmo.serenity.member.dto.PinVerifyRequest;
import com.bysmo.serenity.member.service.PinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/members/pin")
@RequiredArgsConstructor
@Tag(name = "PIN", description = "PIN management APIs")
public class PinController {

    private final PinService pinService;

    @PostMapping("/setup")
    @Operation(summary = "Setup PIN for a member")
    public ResponseEntity<Map<String, Object>> setupPin(
            @RequestParam UUID membreId,
            @Valid @RequestBody PinSetupRequest request) {
        log.info("POST /api/v1/members/pin/setup for member: {}", membreId);
        pinService.setupPin(membreId, request);
        return ResponseEntity.ok(Map.of(
                "message", "PIN configuré avec succès",
                "membreId", membreId
        ));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify PIN")
    public ResponseEntity<Map<String, Object>> verifyPin(
            @RequestParam UUID membreId,
            @Valid @RequestBody PinVerifyRequest request) {
        log.info("POST /api/v1/members/pin/verify for member: {}", membreId);
        boolean verified = pinService.verifyPin(membreId, request.getCodePin());
        return ResponseEntity.ok(Map.of(
                "verified", verified,
                "membreId", membreId,
                "pinVerified", verified
        ));
    }

    @PostMapping("/enable")
    @Operation(summary = "Enable PIN")
    public ResponseEntity<Map<String, Object>> enablePin(
            @RequestParam UUID membreId,
            @Valid @RequestBody PinEnableRequest request) {
        log.info("POST /api/v1/members/pin/enable for member: {}", membreId);
        pinService.enablePin(membreId, request.getEnable());
        if (request.getMode() != null) {
            pinService.changePinMode(membreId, request.getMode());
        }
        return ResponseEntity.ok(Map.of(
                "message", request.getEnable() ? "PIN activé" : "PIN désactivé",
                "membreId", membreId
        ));
    }

    @PostMapping("/disable")
    @Operation(summary = "Disable PIN")
    public ResponseEntity<Map<String, Object>> disablePin(@RequestParam UUID membreId) {
        log.info("POST /api/v1/members/pin/disable for member: {}", membreId);
        pinService.enablePin(membreId, false);
        return ResponseEntity.ok(Map.of(
                "message", "PIN désactivé",
                "membreId", membreId
        ));
    }
}
