package com.serenity.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PiSpiConfigResponse {

    private UUID id;
    private UUID organisationId;
    private String clientId;
    private String clientSecret;
    private String apiKey;
    private String payeAlias;
    private String mode;
    private String callbackUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
