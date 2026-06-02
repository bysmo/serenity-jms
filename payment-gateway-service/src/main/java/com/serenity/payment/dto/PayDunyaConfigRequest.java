package com.serenity.payment.dto;

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
public class PayDunyaConfigRequest {

    private UUID organisationId;

    @NotBlank(message = "Master key is required")
    private String masterKey;

    @NotBlank(message = "Private key is required")
    private String privateKey;

    @NotBlank(message = "Public key is required")
    private String publicKey;

    @NotBlank(message = "Token is required")
    private String token;

    @Builder.Default
    private String mode = "test";

    private String ipnUrl;
}
