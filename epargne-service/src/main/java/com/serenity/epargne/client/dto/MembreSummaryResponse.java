package com.serenity.epargne.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembreSummaryResponse {

    private UUID id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String numeroMembre;
    private Boolean actif;
}
