package com.bysmo.serenity.cotisation.entity;

import com.bysmo.serenity.cotisation.enums.ModePaiement;
import com.bysmo.serenity.cotisation.enums.PaiementStatut;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "paiements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "cotisation_id", nullable = false)
    private UUID cotisationId;

    @Column(name = "membre_id", nullable = false)
    private UUID membreId;

    @Column(name = "montant", nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false, length = 30)
    private ModePaiement modePaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 30)
    @Builder.Default
    private PaiementStatut statut = PaiementStatut.EN_ATTENTE;

    @Column(name = "reference")
    private String reference;

    @Column(name = "wallet_alias_id")
    private UUID walletAliasId;

    @Column(name = "compte_externe_id")
    private UUID compteExterneId;

    @JdbcTypeCode(SqlTypes.JSONB)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    @Column(name = "traite_par")
    private UUID traitePar;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "checksum", length = 64)
    private String checksum;

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
