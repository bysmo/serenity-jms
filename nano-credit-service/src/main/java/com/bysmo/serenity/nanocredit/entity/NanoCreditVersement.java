package com.bysmo.serenity.nanocredit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "nano_credit_versements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NanoCreditVersement {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "nano_credit_id", nullable = false)
    private NanoCredit nanoCredit;

    @ManyToOne
    @JoinColumn(name = "echeance_id")
    private NanoCreditEcheance echeance;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal montant;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dateVersement = LocalDateTime.now();

    @Column(length = 20)
    private String modePaiement;

    @Column(length = 100)
    private String reference;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
