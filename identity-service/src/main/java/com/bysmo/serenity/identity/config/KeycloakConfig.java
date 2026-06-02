package com.bysmo.serenity.identity.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak(KeycloakProperties props) {
        return KeycloakBuilder.builder()
                .serverUrl(props.getServerUrl())
                .realm(props.getRealm())
                .clientId(props.getAdminClientId())
                .username(props.getAdminUsername())
                .password(props.getAdminPassword())
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }
}
