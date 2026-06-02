package com.serenity.admin.dto;

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

    @NotBlank(message = "Name is required")
    private String nom;

    @NotBlank(message = "Subject is required")
    private String sujet;

    @NotBlank(message = "Body is required")
    private String corps;

    private String type;

    private Boolean actif;
}
