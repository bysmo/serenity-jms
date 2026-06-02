package com.serenity.account.controller;

import com.serenity.account.dto.SortieCaisseRequest;
import com.serenity.account.dto.SortieCaisseResponse;
import com.serenity.account.service.CaisseService;
import com.serenity.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts/sorties")
@RequiredArgsConstructor
public class SortieCaisseController {

    private final CaisseService caisseService;

    @PostMapping
    @PreAuthorize("hasRole('GESTION_CAISSES')")
    public ResponseEntity<ApiResponse<SortieCaisseResponse>> createSortie(
            @Valid @RequestBody SortieCaisseRequest request) {
        SortieCaisseResponse response = caisseService.createSortie(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Sortie de caisse effectuée avec succès", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SortieCaisseResponse>>> listSorties() {
        List<SortieCaisseResponse> sorties = caisseService.listSorties();
        return ResponseEntity.ok(ApiResponse.success(sorties));
    }
}
