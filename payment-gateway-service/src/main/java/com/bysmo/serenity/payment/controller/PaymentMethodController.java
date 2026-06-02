package com.bysmo.serenity.payment.controller;

import com.bysmo.serenity.common.dto.ApiResponse;
import com.bysmo.serenity.payment.dto.PaymentMethodRequest;
import com.bysmo.serenity.payment.dto.PaymentMethodResponse;
import com.bysmo.serenity.payment.entity.enums.PaymentGateway;
import com.bysmo.serenity.payment.service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment-gateways/methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentMethodResponse>>> listPaymentMethods(
            @RequestParam(required = false) PaymentGateway gateway,
            @RequestParam(required = false) Boolean isActive) {
        List<PaymentMethodResponse> methods = paymentMethodService.listPaymentMethods(gateway, isActive);
        return ResponseEntity.ok(ApiResponse.success(methods));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> getPaymentMethod(@PathVariable UUID id) {
        PaymentMethodResponse method = paymentMethodService.getPaymentMethod(id);
        return ResponseEntity.ok(ApiResponse.success(method));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> getPaymentMethodByCode(@PathVariable String code) {
        PaymentMethodResponse method = paymentMethodService.getPaymentMethodByCode(code);
        return ResponseEntity.ok(ApiResponse.success(method));
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> createPaymentMethod(
            @Valid @RequestBody PaymentMethodRequest request) {
        PaymentMethodResponse method = paymentMethodService.createPaymentMethod(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Payment method created successfully", method));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> updatePaymentMethod(
            @PathVariable UUID id,
            @Valid @RequestBody PaymentMethodRequest request) {
        PaymentMethodResponse method = paymentMethodService.updatePaymentMethod(id, request);
        return ResponseEntity.ok(ApiResponse.success("Payment method updated successfully", method));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTION_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePaymentMethod(@PathVariable UUID id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.ok(ApiResponse.success("Payment method deleted successfully", null));
    }
}
