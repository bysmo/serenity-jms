package com.serenity.keycloak.controller;

import com.serenity.common.dto.ApiResponse;
import com.serenity.keycloak.dto.RealmDto;
import com.serenity.keycloak.service.RealmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/keycloak/realms")
public class RealmController {

    private final RealmService realmService;

    public RealmController(RealmService realmService) {
        this.realmService = realmService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RealmDto>>> listRealms() {
        List<RealmDto> realms = realmService.listRealms();
        return ResponseEntity.ok(ApiResponse.success(realms));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RealmDto>> createRealm(@RequestBody RealmDto realmDto) {
        RealmDto created = realmService.createRealm(realmDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Realm created successfully", created));
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<RealmDto>> getRealm(@PathVariable String name) {
        RealmDto realm = realmService.getRealm(name);
        return ResponseEntity.ok(ApiResponse.success(realm));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponse<Void>> deleteRealm(@PathVariable String name) {
        realmService.deleteRealm(name);
        return ResponseEntity.ok(ApiResponse.success("Realm deleted successfully", null));
    }
}
