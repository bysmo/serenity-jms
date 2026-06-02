package com.bysmo.serenity.epargne.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaisseRequest {

    @NotBlank
    private String nom;

    private String description;

    @NotBlank
    private String type;

    private Boolean actif = true;
}
