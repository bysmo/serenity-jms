package com.serenity.keycloak.service;

import com.serenity.keycloak.config.KeycloakProperties;
import com.serenity.keycloak.dto.UserAttributeDto;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserAttributeService {

    private static final Logger log = LoggerFactory.getLogger(UserAttributeService.class);

    private final Keycloak keycloak;
    private final KeycloakProperties properties;

    public UserAttributeService(Keycloak keycloak, KeycloakProperties properties) {
        this.keycloak = keycloak;
        this.properties = properties;
    }

    private String realm() {
        return properties.getRealm();
    }

    /**
     * Retrieve attributes for a specific user.
     */
    public UserAttributeDto getUserAttributes(String userId) {
        log.info("Getting attributes for user {} in realm: {}", userId, realm());
        UserRepresentation user = keycloak.realm(realm()).users().get(userId).toRepresentation();
        Map<String, List<String>> attributes = user.getAttributes();
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        return new UserAttributeDto(attributes);
    }

    /**
     * Update or set custom user attributes. This merges the provided attributes
     * with existing ones, allowing partial updates without overwriting all attributes.
     */
    public UserAttributeDto updateUserAttributes(String userId, UserAttributeDto attributeDto) {
        log.info("Updating attributes for user {} in realm: {}", userId, realm());
        UserRepresentation user = keycloak.realm(realm()).users().get(userId).toRepresentation();

        Map<String, List<String>> existingAttributes = user.getAttributes();
        if (existingAttributes == null) {
            existingAttributes = new HashMap<>();
        }

        // Merge new attributes into existing ones
        if (attributeDto.getAttributes() != null) {
            existingAttributes.putAll(attributeDto.getAttributes());
        }

        user.setAttributes(existingAttributes);
        keycloak.realm(realm()).users().get(userId).update(user);

        // Retrieve updated user
        UserRepresentation updated = keycloak.realm(realm()).users().get(userId).toRepresentation();
        return new UserAttributeDto(updated.getAttributes() != null ? updated.getAttributes() : new HashMap<>());
    }
}
