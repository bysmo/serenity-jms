package com.bysmo.serenity.payment.repository;

import com.bysmo.serenity.payment.entity.PaymentTransaction;
import com.bysmo.serenity.payment.entity.enums.PaymentGateway;
import com.bysmo.serenity.payment.entity.enums.TransactionStatut;
import com.bysmo.serenity.payment.entity.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    Optional<PaymentTransaction> findByReference(String reference);

    Optional<PaymentTransaction> findByExternalReference(String externalReference);

    List<PaymentTransaction> findByStatutAndCreatedAtBefore(TransactionStatut statut, LocalDateTime createdAt);

    Page<PaymentTransaction> findByGateway(PaymentGateway gateway, Pageable pageable);

    Page<PaymentTransaction> findByStatut(TransactionStatut statut, Pageable pageable);

    Page<PaymentTransaction> findByGatewayAndStatut(PaymentGateway gateway, TransactionStatut statut, Pageable pageable);

    Page<PaymentTransaction> findByTransactionType(TransactionType transactionType, Pageable pageable);

    List<PaymentTransaction> findByInternalReference(String internalReference);

    List<PaymentTransaction> findByMembreId(UUID membreId);

    Page<PaymentTransaction> findByOrganisationId(UUID organisationId, Pageable pageable);

    boolean existsByReference(String reference);
}
