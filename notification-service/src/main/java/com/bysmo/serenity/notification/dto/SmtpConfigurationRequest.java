package com.bysmo.serenity.notification.dto;

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
public class SmtpConfigurationRequest {

    @NotBlank(message = "L'hôte SMTP est obligatoire")
    private String host;

    @NotNull(message = "Le port SMTP est obligatoire")
    private Integer port;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String username;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    @Builder.Default
    private Boolean authEnabled = true;

    @Builder.Default
    private Boolean starttlsEnabled = true;

    @Builder.Default
    private Boolean sslEnabled = false;

    @NotBlank(message = "L'email expéditeur est obligatoire")
    private String fromEmail;

    private String fromName;

    @Builder.Default
    private Boolean actif = true;
}
