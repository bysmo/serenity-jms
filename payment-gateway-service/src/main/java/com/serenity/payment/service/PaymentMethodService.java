package com.serenity.payment.service;

import com.serenity.common.exception.EntityNotFoundException;
import com.serenity.payment.dto.PaymentMethodRequest;
import com.serenity.payment.dto.PaymentMethodResponse;
import com.serenity.payment.entity.PaymentMethod;
import com.serenity.payment.entity.enums.PaymentGateway;
import com.serenity.payment.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> listPaymentMethods(PaymentGateway gateway, Boolean isActive) {
        List<PaymentMethod> methods;
        if (gateway != null && isActive != null) {
            methods = paymentMethodRepository.findByGatewayAndIsActive(gateway, isActive);
        } else if (gateway != null) {
            methods = paymentMethodRepository.findByGateway(gateway);
        } else if (isActive != null) {
            methods = paymentMethodRepository.findByIsActive(isActive);
        } else {
            methods = paymentMethodRepository.findAll();
        }
        return methods.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PaymentMethodResponse getPaymentMethod(UUID id) {
        PaymentMethod method = findPaymentMethodOrThrow(id);
        return toResponse(method);
    }

    @Transactional(readOnly = true)
    public PaymentMethodResponse getPaymentMethodByCode(String code) {
        PaymentMethod method = paymentMethodRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("PaymentMethod", code));
        return toResponse(method);
    }

    public PaymentMethodResponse createPaymentMethod(PaymentMethodRequest request) {
        if (paymentMethodRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Payment method with code '" + request.getCode() + "' already exists");
        }

        PaymentMethod method = PaymentMethod.builder()
                .id(UUID.randomUUID())
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .gateway(request.getGateway())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .minAmount(request.getMinAmount())
                .maxAmount(request.getMaxAmount())
                .feesPercentage(request.getFeesPercentage())
                .feesFixed(request.getFeesFixed())
                .config(request.getConfig())
                .build();

        PaymentMethod saved = paymentMethodRepository.save(method);
        log.info("Created payment method: {} ({})", saved.getCode(), saved.getId());
        return toResponse(saved);
    }

    public PaymentMethodResponse updatePaymentMethod(UUID id, PaymentMethodRequest request) {
        PaymentMethod method = findPaymentMethodOrThrow(id);

        method.setName(request.getName());
        method.setDescription(request.getDescription());
        method.setGateway(request.getGateway());
        if (request.getIsActive() != null) {
            method.setIsActive(request.getIsActive());
        }
        method.setMinAmount(request.getMinAmount());
        method.setMaxAmount(request.getMaxAmount());
        method.setFeesPercentage(request.getFeesPercentage());
        method.setFeesFixed(request.getFeesFixed());
        if (request.getConfig() != null) {
            method.setConfig(request.getConfig());
        }

        PaymentMethod saved = paymentMethodRepository.save(method);
        log.info("Updated payment method: {} ({})", saved.getCode(), saved.getId());
        return toResponse(saved);
    }

    public void deletePaymentMethod(UUID id) {
        PaymentMethod method = findPaymentMethodOrThrow(id);
        paymentMethodRepository.delete(method);
        log.info("Deleted payment method: {} ({})", method.getCode(), method.getId());
    }

    private PaymentMethod findPaymentMethodOrThrow(UUID id) {
        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PaymentMethod", id.toString()));
    }

    private PaymentMethodResponse toResponse(PaymentMethod method) {
        return PaymentMethodResponse.builder()
                .id(method.getId())
                .code(method.getCode())
                .name(method.getName())
                .description(method.getDescription())
                .gateway(method.getGateway())
                .isActive(method.getIsActive())
                .minAmount(method.getMinAmount())
                .maxAmount(method.getMaxAmount())
                .feesPercentage(method.getFeesPercentage())
                .feesFixed(method.getFeesFixed())
                .config(method.getConfig())
                .createdAt(method.getCreatedAt())
                .updatedAt(method.getUpdatedAt())
                .build();
    }
}
