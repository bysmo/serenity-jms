package com.serenity.keycloak.service;

import com.serenity.keycloak.config.KeycloakProperties;
import com.serenity.keycloak.dto.RealmDto;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RealmService {

    private static final Logger log = LoggerFactory.getLogger(RealmService.class);

    private final Keycloak keycloak;
    private final KeycloakProperties properties;

    public RealmService(Keycloak keycloak, KeycloakProperties properties) {
        this.keycloak = keycloak;
        this.properties = properties;
    }

    /**
     * List all realms in the Keycloak instance.
     */
    public List<RealmDto> listRealms() {
        log.info("Listing all realms");
        return keycloak.realms().findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new realm.
     */
    public RealmDto createRealm(RealmDto realmDto) {
        log.info("Creating realm: {}", realmDto.getRealmName());
        RealmRepresentation realmRep = new RealmRepresentation();
        realmRep.setRealm(realmDto.getRealmName());
        realmRep.setEnabled(realmDto.getEnabled() != null ? realmDto.getEnabled() : true);
        realmRep.setDisplayName(realmDto.getDisplayName());

        keycloak.realms().create(realmRep);

        RealmRepresentation created = keycloak.realms().realm(realmDto.getRealmName()).toRepresentation();
        return toDto(created);
    }

    /**
     * Get details of a specific realm.
     */
    public RealmDto getRealm(String realmName) {
        log.info("Getting realm: {}", realmName);
        try {
            RealmRepresentation realm = keycloak.realms().realm(realmName).toRepresentation();
            return toDto(realm);
        } catch (Exception e) {
            throw new RuntimeException("Realm not found: " + realmName, e);
        }
    }

    /**
     * Delete a realm.
     */
    public void deleteRealm(String realmName) {
        log.info("Deleting realm: {}", realmName);
        keycloak.realms().realm(realmName).remove();
    }

    private RealmDto toDto(RealmRepresentation realm) {
        RealmDto dto = new RealmDto();
        dto.setRealmName(realm.getRealm());
        dto.setDisplayName(realm.getDisplayName());
        dto.setEnabled(realm.isEnabled());
        return dto;
    }
}
