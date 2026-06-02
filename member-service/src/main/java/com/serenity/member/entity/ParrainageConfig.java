package com.serenity.member.entity;

import com.serenity.member.entity.enums.DeclencheurParrainage;
import com.serenity.member.entity.enums.TypeRemuneration;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parrainage_configs")
public class ParrainageConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_remuneration", nullable = false, length = 30)
    @Builder.Default
    private TypeRemuneration typeRemuneration = TypeRemuneration.FIXE;

    @Column(name = "montant_fixe", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal montantFixe = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal pourcentage = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private DeclencheurParrainage declencheur = DeclencheurParrainage.INSCRIPTION;

    @Column(name = "niveau_max", nullable = false)
    @Builder.Default
    private Integer niveauMax = 1;

    @Column(name = "delai_disponibilite_jours", nullable = false)
    @Builder.Default
    private Integer delaiDisponibiliteJours = 30;

    @Column(name = "plafond_mensuel", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal plafondMensuel = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
