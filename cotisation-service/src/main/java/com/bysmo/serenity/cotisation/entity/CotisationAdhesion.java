package com.bysmo.serenity.cotisation.entity;

import com.bysmo.serenity.cotisation.enums.AdhesionStatut;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cotisation_adhesions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CotisationAdhesion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "cotisation_id", nullable = false)
    private UUID cotisationId;

    @Column(name = "membre_id", nullable = false)
    private UUID membreId;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 30)
    @Builder.Default
    private AdhesionStatut statut = AdhesionStatut.EN_ATTENTE;

    @Column(name = "traite_par")
    private UUID traitePar;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "motif_refus", columnDefinition = "TEXT")
    private String motifRefus;

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
