package com.bysmo.serenity.nanocredit.entity;

import com.bysmo.serenity.nanocredit.entity.enums.GarantStatut;
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
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "nano_credit_garants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NanoCreditGarant {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "nano_credit_id", nullable = false)
    private NanoCredit nanoCredit;

    @Column(nullable = false)
    private UUID garantMembreId;

    @Column
    @Builder.Default
    private Integer qualite = 0;

    @Column(precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal soldeGarantie = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal pourcentagePartage;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GarantStatut statut = GarantStatut.ACTIF;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
