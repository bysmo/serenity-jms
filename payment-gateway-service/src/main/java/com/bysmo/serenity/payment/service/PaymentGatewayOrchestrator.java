package com.bysmo.serenity.payment.service;

import com.bysmo.serenity.common.exception.EntityNotFoundException;
import com.bysmo.serenity.payment.dto.CollectionRequest;
import com.bysmo.serenity.payment.dto.DisbursementRequest;
import com.bysmo.serenity.payment.dto.PaymentTransactionResponse;
import com.bysmo.serenity.payment.entity.PaymentTransaction;
import com.bysmo.serenity.payment.entity.enums.PaymentGateway;
import com.bysmo.serenity.payment.entity.enums.TransactionStatut;
import com.bysmo.serenity.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PaymentGatewayOrchestrator {

    private final PayDunyaService payDunyaService;
    private final PiSpiService piSpiService;
    private final PaymentTransactionRepository paymentTransactionRepository;

    /**
     * Process disbursement via specified gateway
     */
    public PaymentTransaction processDisbursement(DisbursementRequest request) {
        log.info("Processing disbursement: telephone={}, montant={}, gateway={}, withdrawMode={}",
                request.getTelephone(), request.getMontant(), request.getGateway(), request.getWithdrawMode());

        return switch (request.getGateway()) {
            case PAYDUNYA -> payDunyaService.disburse(
                    request.getTelephone(),
                    request.getMontant(),
                    request.getWithdrawMode(),
                    request.getInternalReference()
            );
            case PISPI -> piSpiService.sendMoney(
                    request.getTelephone(),
                    request.getMontant(),
                    request.getWithdrawMode(),
                    request.getInternalReference()
            );
        };
    }

    /**
     * Process collection via specified gateway
     */
    public PaymentTransaction processCollection(CollectionRequest request) {
        log.info("Processing collection: telephone={}, montant={}, gateway={}",
                request.getTelephone(), request.getMontant(), request.getGateway());

        return switch (request.getGateway()) {
            case PAYDUNYA -> payDunyaService.createCheckout(
                    request.getMontant(),
                    "Collection via PayDunya",
                    request.getInternalReference()
            );
            case PISPI -> piSpiService.collect(
                    request.getTelephone(),
                    request.getMontant(),
                    request.getInternalReference()
            );
        };
    }

    /**
     * Get transaction by reference
     */
    @Transactional(readOnly = true)
    public PaymentTransaction getTransaction(String reference) {
        return paymentTransactionRepository.findByReference(reference)
                .orElseThrow(() -> new EntityNotFoundException("PaymentTransaction", reference));
    }

    /**
     * Get transactions with filters
     */
    @Transactional(readOnly = true)
    public Page<PaymentTransaction> getTransactions(String gateway, String statut, Pageable pageable) {
        if (gateway != null && statut != null) {
            PaymentGateway gatewayEnum = PaymentGateway.valueOf(gateway);
            TransactionStatut statutEnum = TransactionStatut.valueOf(statut);
            return paymentTransactionRepository.findByGatewayAndStatut(gatewayEnum, statutEnum, pageable);
        } else if (gateway != null) {
            PaymentGateway gatewayEnum = PaymentGateway.valueOf(gateway);
            return paymentTransactionRepository.findByGateway(gatewayEnum, pageable);
        } else if (statut != null) {
            TransactionStatut statutEnum = TransactionStatut.valueOf(statut);
            return paymentTransactionRepository.findByStatut(statutEnum, pageable);
        } else {
            return paymentTransactionRepository.findAll(pageable);
        }
    }

    /**
     * Convert transaction entity to response DTO
     */
    public PaymentTransactionResponse toResponse(PaymentTransaction transaction) {
        return PaymentTransactionResponse.builder()
                .id(transaction.getId())
                .reference(transaction.getReference())
                .externalReference(transaction.getExternalReference())
                .gateway(transaction.getGateway())
                .transactionType(transaction.getTransactionType())
                .statut(transaction.getStatut())
                .telephone(transaction.getTelephone())
                .montant(transaction.getMontant())
                .fees(transaction.getFees())
                .netAmount(transaction.getNetAmount())
                .currency(transaction.getCurrency())
                .withdrawMode(transaction.getWithdrawMode())
                .description(transaction.getDescription())
                .internalReference(transaction.getInternalReference())
                .gatewayResponse(transaction.getGatewayResponse())
                .callbackData(transaction.getCallbackData())
                .errorMessage(transaction.getErrorMessage())
                .organisationId(transaction.getOrganisationId())
                .membreId(transaction.getMembreId())
                .createdBy(transaction.getCreatedBy())
                .confirmedAt(transaction.getConfirmedAt())
                .failedAt(transaction.getFailedAt())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
