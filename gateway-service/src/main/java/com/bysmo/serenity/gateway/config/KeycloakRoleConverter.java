package com.bysmo.serenity.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reactive-compatible Keycloak role converter that extracts realm roles from a JWT token
 * and converts them to Spring Security GrantedAuthority objects with the "ROLE_" prefix.
 *
 * This converter is used by the gateway's SecurityConfig to integrate with
 * Keycloak-issued JWT tokens in a reactive (WebFlux) context.
 */
@Slf4j
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_KEY = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS_CLAIM);

        if (realmAccess == null || realmAccess.isEmpty()) {
            log.debug("No realm_access claim found in JWT token");
            return Collections.emptyList();
        }

        List<String> roles = (List<String>) realmAccess.get(ROLES_KEY);

        if (roles == null || roles.isEmpty()) {
            log.debug("No roles found in realm_access claim");
            return Collections.emptyList();
        }

        Collection<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .collect(Collectors.toList());

        log.debug("Extracted authorities from JWT: {}", authorities);
        return authorities;
    }
}
