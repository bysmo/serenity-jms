package com.bysmo.serenity.nanocredit.entity;

import com.bysmo.serenity.nanocredit.entity.enums.EcheanceStatut;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "nano_credit_echeances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NanoCreditEcheance {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "nano_credit_id", nullable = false)
    private NanoCredit nanoCredit;

    @Column(nullable = false)
    private Integer numeroEcheance;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal montant;

    @Column(precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal montantPenalite = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate dateEcheance;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EcheanceStatut statut = EcheanceStatut.EN_ATTENTE;

    @Column
    private LocalDateTime datePaiement;

    @Column(precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal montantPaye = BigDecimal.ZERO;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
