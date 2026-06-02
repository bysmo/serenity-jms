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
public class PayDunyaConfigResponse {

    private UUID id;
    private UUID organisationId;
    private String masterKey;
    private String privateKey;
    private String publicKey;
    private String token;
    private String mode;
    private String ipnUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
