package com.serenity.notification.dto;

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
public class SmtpConfigurationResponse {

    private UUID id;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private Boolean authEnabled;
    private Boolean starttlsEnabled;
    private Boolean sslEnabled;
    private String fromEmail;
    private String fromName;
    private Boolean actif;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
