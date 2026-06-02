package com.bysmo.serenity.collector.entity;

import com.bysmo.serenity.collector.entity.enums.TypeCollecte;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "collectes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collecte {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "collecte_session_id", nullable = false)
    private UUID collecteSessionId;

    @Column(name = "membre_id", nullable = false)
    private UUID membreId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_collecte", nullable = false, length = 30)
    private TypeCollecte typeCollecte;

    @Column(name = "montant", nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(name = "echeance_type", length = 50)
    private String echeanceType;

    @Column(name = "echeance_id")
    private UUID echeanceId;

    @Column(name = "otp_code", length = 6)
    private String otpCode;

    @Column(name = "is_confirmed", nullable = false)
    @Builder.Default
    private Boolean isConfirmed = false;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "reference_transaction", length = 100)
    private String referenceTransaction;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
