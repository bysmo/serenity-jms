package com.bysmo.serenity.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PiSpiConfigRequest {

    private UUID organisationId;

    @NotBlank(message = "Client ID is required")
    private String clientId;

    @NotBlank(message = "Client secret is required")
    private String clientSecret;

    @NotBlank(message = "API key is required")
    private String apiKey;

    @NotBlank(message = "Paye alias is required")
    private String payeAlias;

    @Builder.Default
    private String mode = "test";

    private String callbackUrl;
}
