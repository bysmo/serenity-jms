package com.serenity.nanocredit.entity;

import com.serenity.nanocredit.entity.enums.FrequenceRemboursement;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "nano_credit_paliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NanoCreditPalier {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, length = 50)
    private String numero;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(precision = 19, scale = 4)
    private BigDecimal montantPlafond;

    @Column
    private Integer dureeJours;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private FrequenceRemboursement frequenceRemboursement;

    @Column(precision = 5, scale = 2)
    private BigDecimal tauxInteret;

    @Column(precision = 19, scale = 4)
    private BigDecimal penaliteParJour;

    @Column(precision = 19, scale = 4)
    private BigDecimal minMontantTotalRembourse;

    @Column(precision = 19, scale = 4)
    private BigDecimal minEpargneCumulee;

    @Column(precision = 5, scale = 2)
    private BigDecimal minEpargnePercent;

    @Column
    private Integer minGarantQualite;

    @Column(precision = 5, scale = 2)
    private BigDecimal pourcentagePartageGarant;

    @Column
    @Builder.Default
    private Boolean actif = true;

    @Column(length = 64)
    private String checksum;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
