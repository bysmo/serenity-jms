package com.serenity.cotisation.entity;

import com.serenity.cotisation.enums.CotisationType;
import com.serenity.cotisation.enums.Frequence;
import com.serenity.cotisation.enums.TypeMontant;
import com.serenity.cotisation.enums.Visibilite;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cotisations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cotisation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private CotisationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequence", nullable = false, length = 30)
    private Frequence frequence;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_montant", nullable = false, length = 30)
    private TypeMontant typeMontant;

    @Column(name = "montant", precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(name = "caisse_id", nullable = false)
    private UUID caisseId;

    @Column(name = "created_by_membre_id")
    private UUID createdByMembreId;

    @Column(name = "admin_membre_id")
    private UUID adminMembreId;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibilite", nullable = false, length = 20)
    @Builder.Default
    private Visibilite visibilite = Visibilite.PUBLIQUE;

    @Column(name = "tag", length = 100)
    private String tag;

    @Column(name = "actif", nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "checksum", length = 64)
    private String checksum;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

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
