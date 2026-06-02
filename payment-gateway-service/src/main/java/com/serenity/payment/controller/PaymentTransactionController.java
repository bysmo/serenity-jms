package com.serenity.payment.controller;

import com.serenity.common.dto.ApiResponse;
import com.serenity.common.dto.PagedResponse;
import com.serenity.payment.dto.CollectionRequest;
import com.serenity.payment.dto.DisbursementRequest;
import com.serenity.payment.dto.PaymentTransactionResponse;
import com.serenity.payment.entity.PaymentTransaction;
import com.serenity.payment.service.PaymentGatewayOrchestrator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment-gateways/transactions")
@RequiredArgsConstructor
public class PaymentTransactionController {

    private final PaymentGatewayOrchestrator orchestrator;

    @PostMapping("/disburse")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentTransactionResponse>> processDisbursement(
            @Valid @RequestBody DisbursementRequest request) {
        PaymentTransaction transaction = orchestrator.processDisbursement(request);
        PaymentTransactionResponse response = orchestrator.toResponse(transaction);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Disbursement processed successfully", response));
    }

    @PostMapping("/collect")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentTransactionResponse>> processCollection(
            @Valid @RequestBody CollectionRequest request) {
        PaymentTransaction transaction = orchestrator.processCollection(request);
        PaymentTransactionResponse response = orchestrator.toResponse(transaction);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Collection processed successfully", response));
    }

    @GetMapping("/{reference}")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentTransactionResponse>> getTransaction(@PathVariable String reference) {
        PaymentTransaction transaction = orchestrator.getTransaction(reference);
        PaymentTransactionResponse response = orchestrator.toResponse(transaction);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentTransactionResponse>>> getTransactions(
            @RequestParam(required = false) String gateway,
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PaymentTransaction> transactions = orchestrator.getTransactions(gateway, statut, pageable);
        Page<PaymentTransactionResponse> responsePage = transactions.map(orchestrator::toResponse);

        PagedResponse<PaymentTransactionResponse> pagedResponse = PagedResponse.of(
                responsePage.getContent(),
                responsePage.getNumber(),
                responsePage.getSize(),
                responsePage.getTotalElements(),
                responsePage.getTotalPages(),
                responsePage.isLast()
        );

        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }
}
