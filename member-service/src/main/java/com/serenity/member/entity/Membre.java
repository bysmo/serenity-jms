package com.serenity.member.entity;

import com.serenity.member.entity.enums.MembreStatut;
import com.serenity.member.entity.enums.PinMode;
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
@Table(name = "membres")
public class Membre {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(unique = true, length = 255)
    private String email;

    @Column(nullable = false, unique = true, length = 20)
    private String telephone;

    @Column(length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private MembreStatut statut = MembreStatut.EN_ATTENTE;

    @Column(name = "segment_id", nullable = false)
    private UUID segmentId;

    // Location fields
    @Column(name = "adresse_ligne1", length = 255)
    private String adresseLigne1;

    @Column(name = "adresse_ligne2", length = 255)
    private String adresseLigne2;

    @Column(length = 100)
    private String ville;

    @Column(length = 100)
    private String region;

    @Column(name = "code_postal", length = 20)
    private String codePostal;

    @Column(nullable = false, length = 100)
    @Builder.Default
    private String pays = "SN";

    private Double latitude;

    private Double longitude;

    // PIN fields
    @Column(name = "code_pin", length = 255)
    private String codePin;

    @Column(name = "pin_enabled", nullable = false)
    @Builder.Default
    private Boolean pinEnabled = false;

    @Column(name = "pin_attempts", nullable = false)
    @Builder.Default
    private Integer pinAttempts = 0;

    @Column(name = "pin_locked_until")
    private LocalDateTime pinLockedUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "pin_mode", nullable = false, length = 20)
    @Builder.Default
    private PinMode pinMode = PinMode.EACH_TIME;

    // Nano-credit fields
    @Column(name = "nano_credit_eligible", nullable = false)
    @Builder.Default
    private Boolean nanoCreditEligible = false;

    @Column(name = "nano_credit_limite", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal nanoCreditLimite = BigDecimal.ZERO;

    @Column(name = "nano_credit_solde", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal nanoCreditSolde = BigDecimal.ZERO;

    // Parrainage fields
    @Column(name = "parrain_id")
    private UUID parrainId;

    @Column(name = "code_parrainage", unique = true, length = 20)
    private String codeParrainage;

    @Column(name = "parrainage_actif", nullable = false)
    @Builder.Default
    private Boolean parrainageActif = false;

    @Column(name = "niveau_parrainage", nullable = false)
    @Builder.Default
    private Integer niveauParrainage = 0;

    // Verification fields
    @Column(name = "email_verifie", nullable = false)
    @Builder.Default
    private Boolean emailVerifie = false;

    @Column(name = "telephone_verifie", nullable = false)
    @Builder.Default
    private Boolean telephoneVerifie = false;

    @Column(name = "kyc_niveau", nullable = false, length = 20)
    @Builder.Default
    private String kycNiveau = "NONE";

    // Push notification fields
    @Column(name = "push_token", length = 500)
    private String pushToken;

    @Column(name = "push_enabled", nullable = false)
    @Builder.Default
    private Boolean pushEnabled = true;

    // Checksum
    @Column(length = 64)
    private String checksum;

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
