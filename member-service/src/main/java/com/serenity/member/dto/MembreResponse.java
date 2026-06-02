package com.serenity.member.dto;

import com.serenity.member.entity.enums.MembreStatut;
import com.serenity.member.entity.enums.PinMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembreResponse {

    private UUID id;
    private String numero;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private MembreStatut statut;
    private UUID segmentId;

    // Location fields
    private String adresseLigne1;
    private String adresseLigne2;
    private String ville;
    private String region;
    private String codePostal;
    private String pays;
    private Double latitude;
    private Double longitude;

    // PIN fields
    private Boolean pinEnabled;
    private PinMode pinMode;

    // Nano-credit fields
    private Boolean nanoCreditEligible;
    private BigDecimal nanoCreditLimite;
    private BigDecimal nanoCreditSolde;

    // Parrainage fields
    private UUID parrainId;
    private String codeParrainage;
    private Boolean parrainageActif;
    private Integer niveauParrainage;

    // Verification fields
    private Boolean emailVerifie;
    private Boolean telephoneVerifie;
    private String kycNiveau;

    // Push
    private Boolean pushEnabled;

    private String checksum;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
