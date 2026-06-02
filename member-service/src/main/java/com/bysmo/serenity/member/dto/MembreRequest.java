package com.bysmo.serenity.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembreRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100)
    private String prenom;

    @Size(max = 255)
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 20)
    private String telephone;

    private String password;

    private UUID segmentId;

    // Location fields
    private String adresseLigne1;
    private String adresseLigne2;
    private String ville;
    private String region;
    private String codePostal;
    @Builder.Default
    private String pays = "SN";
    private Double latitude;
    private Double longitude;

    // Parrainage
    private String codeParrainage;
}
