package com.bysmo.serenity.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailTemplateRequest {

    @NotBlank(message = "Le nom du template est obligatoire")
    private String nom;

    @NotBlank(message = "Le sujet est obligatoire")
    private String sujet;

    @NotBlank(message = "Le corps est obligatoire")
    private String corps;

    private String type;

    @Builder.Default
    private Boolean actif = true;
}
