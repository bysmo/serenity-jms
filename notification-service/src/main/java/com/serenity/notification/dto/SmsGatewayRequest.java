package com.serenity.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class SmsGatewayRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le code provider est obligatoire")
    private String providerCode;

    @NotBlank(message = "L'URL API est obligatoire")
    private String apiUrl;

    @NotBlank(message = "La clé API est obligatoire")
    private String apiKey;

    @NotBlank(message = "Le nom de l'expéditeur est obligatoire")
    private String senderName;

    @Builder.Default
    private Boolean isActive = true;

    @NotNull(message = "L'ordre est obligatoire")
    private Integer ordre;

    @Builder.Default
    private Integer maxRetries = 3;

    @Builder.Default
    private Integer timeoutSeconds = 30;
}
