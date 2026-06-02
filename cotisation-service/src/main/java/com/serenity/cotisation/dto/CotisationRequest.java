package com.serenity.cotisation.dto;

import com.serenity.cotisation.enums.CotisationType;
import com.serenity.cotisation.enums.Frequence;
import com.serenity.cotisation.enums.TypeMontant;
import com.serenity.cotisation.enums.Visibilite;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CotisationRequest {

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    private String description;

    @NotNull(message = "Le type est obligatoire")
    private CotisationType type;

    @NotNull(message = "La fréquence est obligatoire")
    private Frequence frequence;

    @NotNull(message = "Le type de montant est obligatoire")
    private TypeMontant typeMontant;

    private BigDecimal montant;

    @NotNull(message = "La caisse est obligatoire")
    private UUID caisseId;

    private UUID createdByMembreId;

    private UUID adminMembreId;

    @Builder.Default
    private Visibilite visibilite = Visibilite.PUBLIQUE;

    private String tag;

    @Builder.Default
    private Boolean actif = true;

    private LocalDate dateDebut;

    private LocalDate dateFin;
}
