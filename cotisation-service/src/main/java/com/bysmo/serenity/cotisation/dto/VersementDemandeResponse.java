package com.bysmo.serenity.cotisation.dto;

import com.bysmo.serenity.cotisation.enums.VersementDemandeStatut;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersementDemandeResponse {

    private UUID id;
    private UUID cotisationId;
    private UUID membreId;
    private BigDecimal montantDemande;
    private VersementDemandeStatut statut;
    private UUID traitePar;
    private LocalDateTime dateTraitement;
    private String motifRejet;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
