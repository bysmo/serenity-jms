package com.bysmo.serenity.member.dto;

import com.bysmo.serenity.member.entity.enums.KycDocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycDocumentRequest {

    @NotNull(message = "Le type de document est obligatoire")
    private KycDocumentType typeDocument;

    @NotBlank(message = "Le nom du fichier est obligatoire")
    private String nomFichier;

    @NotBlank(message = "L'URL du fichier est obligatoire")
    private String urlFichier;

    private Long tailleFichier;
    private String typeMime;
}
