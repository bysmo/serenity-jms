package com.bysmo.serenity.nanocredit.entity;

import com.bysmo.serenity.nanocredit.entity.enums.NanoCreditStatut;
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
@Table(name = "nano_credits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NanoCredit {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID membreId;

    @ManyToOne
    @JoinColumn(name = "palier_id")
    private NanoCreditPalier palier;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal montant;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NanoCreditStatut statut = NanoCreditStatut.DEMANDE_EN_ATTENTE;

    @Column(length = 50)
    private String withdrawMode;

    @Column(precision = 5, scale = 2)
    private BigDecimal scoreAi;

    @Column(precision = 5, scale = 2)
    private BigDecimal scoreHumain;

    @Column(precision = 5, scale = 2)
    private BigDecimal scoreGlobal;

    private UUID compteRemboursementId;

    private UUID compteCreditId;

    private UUID compteImpayeId;

    @Column
    private LocalDateTime dateOctroi;

    @Column
    private LocalDate dateFinRemboursement;

    @Column(precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal montantPenalite = BigDecimal.ZERO;

    @Column
    @Builder.Default
    private Integer joursRetard = 0;

    @Column
    private LocalDateTime dateDernierCalculPenalite;

    @Column
    private UUID createdBy;

    @Column(length = 64)
    private String checksum;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
