package com.serenity.payment.entity;

import com.serenity.payment.entity.enums.PaymentGateway;
import com.serenity.payment.entity.enums.TransactionStatut;
import com.serenity.payment.entity.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    @Id
    private UUID id;

    @Column(name = "reference", nullable = false, unique = true, length = 100)
    private String reference;

    @Column(name = "external_reference", length = 255)
    private String externalReference;

    @Column(name = "gateway", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentGateway gateway;

    @Column(name = "transaction_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "statut", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionStatut statut = TransactionStatut.PENDING;

    @Column(name = "telephone", length = 30)
    private String telephone;

    @Column(name = "montant", nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(name = "fees", precision = 19, scale = 2)
    private BigDecimal fees;

    @Column(name = "net_amount", precision = 19, scale = 2)
    private BigDecimal netAmount;

    @Column(name = "currency", nullable = false, length = 10)
    @Builder.Default
    private String currency = "XOF";

    @Column(name = "withdraw_mode", length = 50)
    private String withdrawMode;

    @Column(name = "description")
    private String description;

    @Column(name = "internal_reference", length = 100)
    private String internalReference;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "gateway_response", columnDefinition = "jsonb")
    private Map<String, Object> gatewayResponse;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "callback_data", columnDefinition = "jsonb")
    private Map<String, Object> callbackData;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "organisation_id")
    private UUID organisationId;

    @Column(name = "membre_id")
    private UUID membreId;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentTransaction other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
