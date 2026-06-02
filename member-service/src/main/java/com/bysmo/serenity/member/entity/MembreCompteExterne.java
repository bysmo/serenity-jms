package com.bysmo.serenity.member.entity;

import com.bysmo.serenity.member.entity.enums.CompteExterneType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "membre_comptes_externes")
public class MembreCompteExterne {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "membre_id", nullable = false)
    private UUID membreId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CompteExterneType type;

    @Column(nullable = false, length = 255)
    private String identifiant;

    @Column(length = 255)
    private String libelle;

    @Column(length = 100)
    private String fournisseur;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

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
