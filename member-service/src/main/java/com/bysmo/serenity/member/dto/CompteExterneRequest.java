package com.bysmo.serenity.member.dto;

import com.bysmo.serenity.member.entity.enums.CompteExterneType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompteExterneRequest {

    @NotNull(message = "Le type est obligatoire")
    private CompteExterneType type;

    @NotBlank(message = "L'identifiant est obligatoire")
    @Size(max = 255)
    private String identifiant;

    @Size(max = 255)
    private String libelle;

    @Size(max = 100)
    private String fournisseur;

    @Builder.Default
    private Boolean isDefault = false;
}
