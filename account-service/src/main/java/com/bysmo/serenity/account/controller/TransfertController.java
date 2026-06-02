package com.bysmo.serenity.account.controller;

import com.bysmo.serenity.account.dto.TransfertRequest;
import com.bysmo.serenity.account.dto.TransfertResponse;
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
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransfertController {

    private final CaisseService caisseService;

    @PostMapping
    @PreAuthorize("hasRole('GESTION_CAISSES')")
    public ResponseEntity<ApiResponse<TransfertResponse>> createTransfert(
            @Valid @RequestBody TransfertRequest request) {
        TransfertResponse response = caisseService.createTransfert(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Transfert effectué avec succès", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransfertResponse>>> listTransferts() {
        List<TransfertResponse> transferts = caisseService.listTransferts();
        return ResponseEntity.ok(ApiResponse.success(transferts));
    }
}
