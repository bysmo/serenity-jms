package com.bysmo.serenity.member.entity;

import com.bysmo.serenity.member.entity.enums.CommissionStatut;
import com.bysmo.serenity.member.entity.enums.DeclencheurParrainage;
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
@Table(name = "parrainage_commissions")
public class ParrainageCommission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "parrain_id", nullable = false)
    private UUID parrainId;

    @Column(name = "filleul_id", nullable = false)
    private UUID filleulId;

    @Column(name = "config_id", nullable = false)
    private UUID configId;

    @Column(nullable = false)
    @Builder.Default
    private Integer niveau = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DeclencheurParrainage declencheur;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private CommissionStatut statut = CommissionStatut.EN_ATTENTE;

    @Column(name = "disponible_le", nullable = false)
    private LocalDateTime disponibleLe;

    @Column(name = "reclame_le")
    private LocalDateTime reclameLe;

    @Column(name = "paye_le")
    private LocalDateTime payeLe;

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
