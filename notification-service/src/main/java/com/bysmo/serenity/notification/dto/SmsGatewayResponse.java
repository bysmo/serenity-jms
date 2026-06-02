package com.bysmo.serenity.notification.dto;

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
public class SmsGatewayResponse {

    private UUID id;
    private String nom;
    private String providerCode;
    private String apiUrl;
    private String apiKey;
    private String senderName;
    private Boolean isActive;
    private Integer ordre;
    private Integer maxRetries;
    private Integer timeoutSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
