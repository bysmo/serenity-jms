package com.bysmo.serenity.account.controller;

import com.bysmo.serenity.account.dto.ApprovisionnementRequest;
import com.bysmo.serenity.account.dto.ApprovisionnementResponse;
import com.bysmo.serenity.account.service.CaisseService;
import com.bysmo.serenity.common.dto.ApiResponse;
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
@RequestMapping("/api/v1/accounts/approvisionnements")
@RequiredArgsConstructor
public class ApprovisionnementController {

    private final CaisseService caisseService;

    @PostMapping
    @PreAuthorize("hasRole('GESTION_CAISSES')")
    public ResponseEntity<ApiResponse<ApprovisionnementResponse>> createApprovisionnement(
            @Valid @RequestBody ApprovisionnementRequest request) {
        ApprovisionnementResponse response = caisseService.createApprovisionnement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Approvisionnement effectué avec succès", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApprovisionnementResponse>>> listApprovisionnements() {
        List<ApprovisionnementResponse> approvisionnements = caisseService.listApprovisionnements();
        return ResponseEntity.ok(ApiResponse.success(approvisionnements));
    }
}
