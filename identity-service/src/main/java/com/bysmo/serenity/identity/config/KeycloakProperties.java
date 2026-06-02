package com.bysmo.serenity.identity.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String realm;
    private String serverUrl;
    private String adminClientId;
    private String adminUsername;
    private String adminPassword;
    private Map<String, String> clientIds;
    private Map<String, String> clientSecrets;
}
