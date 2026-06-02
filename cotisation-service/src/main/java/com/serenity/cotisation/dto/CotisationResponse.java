package com.serenity.cotisation.dto;

import com.serenity.cotisation.enums.CotisationType;
import com.serenity.cotisation.enums.Frequence;
import com.serenity.cotisation.enums.TypeMontant;
import com.serenity.cotisation.enums.Visibilite;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CotisationResponse {

    private UUID id;
    private String libelle;
    private String description;
    private CotisationType type;
    private Frequence frequence;
    private TypeMontant typeMontant;
    private BigDecimal montant;
    private UUID caisseId;
    private UUID createdByMembreId;
    private UUID adminMembreId;
    private Visibilite visibilite;
    private String tag;
    private Boolean actif;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
