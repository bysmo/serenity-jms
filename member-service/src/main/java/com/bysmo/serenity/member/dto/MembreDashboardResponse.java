package com.bysmo.serenity.member.dto;

import com.bysmo.serenity.member.entity.enums.MembreStatut;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembreDashboardResponse {

    private UUID id;
    private String numero;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private MembreStatut statut;
    private String segmentNom;
    private String segmentCouleur;
    private String segmentIcone;

    // Nano-credit summary
    private Boolean nanoCreditEligible;
    private BigDecimal nanoCreditLimite;
    private BigDecimal nanoCreditSolde;

    // KYC status
    private String kycNiveau;

    // Verification
    private Boolean emailVerifie;
    private Boolean telephoneVerifie;

    // PIN
    private Boolean pinEnabled;

    // Parrainage
    private String codeParrainage;
    private Boolean parrainageActif;
    private Integer nombreFilleuls;
    private BigDecimal totalCommissions;
}
