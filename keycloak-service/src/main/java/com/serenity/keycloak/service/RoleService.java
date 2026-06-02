package com.serenity.keycloak.service;

import com.serenity.keycloak.config.KeycloakProperties;
import com.serenity.keycloak.dto.RoleDto;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    private final Keycloak keycloak;
    private final KeycloakProperties properties;

    public RoleService(Keycloak keycloak, KeycloakProperties properties) {
        this.keycloak = keycloak;
        this.properties = properties;
    }

    private String realm() {
        return properties.getRealm();
    }

    /**
     * List all realm roles.
     */
    public List<RoleDto> listRoles() {
        log.info("Listing roles in realm: {}", realm());
        return keycloak.realm(realm()).roles().list().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new realm role.
     */
    public RoleDto createRole(RoleDto roleDto) {
        log.info("Creating role: {} in realm: {}", roleDto.getName(), realm());
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName(roleDto.getName());
        roleRep.setDescription(roleDto.getDescription());
        roleRep.setComposite(roleDto.getComposite() != null ? roleDto.getComposite() : false);

        keycloak.realm(realm()).roles().create(roleRep);

        RoleRepresentation created = keycloak.realm(realm()).roles().get(roleDto.getName()).toRepresentation();
        return toDto(created);
    }

    /**
     * Delete a realm role.
     */
    public void deleteRole(String roleName) {
        log.info("Deleting role: {} in realm: {}", roleName, realm());
        keycloak.realm(realm()).roles().deleteRole(roleName);
    }

    /**
     * Assign a realm role to a user.
     */
    public void assignRoleToUser(String roleName, String userId) {
        log.info("Assigning role {} to user {} in realm: {}", roleName, userId, realm());
        RoleRepresentation roleRep = keycloak.realm(realm()).roles().get(roleName).toRepresentation();
        keycloak.realm(realm()).users().get(userId).roles().realmLevel().add(List.of(roleRep));
    }

    /**
     * Remove a realm role from a user.
     */
    public void removeRoleFromUser(String roleName, String userId) {
        log.info("Removing role {} from user {} in realm: {}", roleName, userId, realm());
        RoleRepresentation roleRep = keycloak.realm(realm()).roles().get(roleName).toRepresentation();
        keycloak.realm(realm()).users().get(userId).roles().realmLevel().remove(List.of(roleRep));
    }

    private RoleDto toDto(RoleRepresentation role) {
        RoleDto dto = new RoleDto();
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setComposite(role.isComposite());
        return dto;
    }
}
