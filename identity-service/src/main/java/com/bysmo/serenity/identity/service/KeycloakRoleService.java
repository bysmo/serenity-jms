package com.bysmo.serenity.identity.service;

import com.bysmo.serenity.common.exception.BusinessException;
import com.bysmo.serenity.common.exception.EntityNotFoundException;
import com.bysmo.serenity.identity.config.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.WebApplicationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakRoleService {

    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;

    /**
     * Assigns a realm role to a user.
     */
    public void assignRole(String userId, String roleName) {
        log.info("Assigning role '{}' to user {}", roleName, userId);

        try {
            RealmResource realmResource = getRealmResource();

            // Verify user exists
            UserResource userResource = realmResource.users().get(userId);
            userResource.toRepresentation(); // throws if user not found

            // Get the realm role
            RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();

            // Assign the role
            userResource.roles().realmLevel().add(Collections.singletonList(role));

            log.info("Role '{}' assigned successfully to user {}", roleName, userId);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                log.error("User or role not found: userId={}, roleName={}", userId, roleName);
                throw new EntityNotFoundException("User or Role", userId + "/" + roleName);
            }
            log.error("Failed to assign role '{}' to user {}: {}", roleName, userId, e.getMessage());
            throw new BusinessException("ROLE_ASSIGN_FAILED", "Failed to assign role: " + roleName);
        } catch (Exception e) {
            log.error("Failed to assign role '{}' to user {}: {}", roleName, userId, e.getMessage());
            throw new BusinessException("ROLE_ASSIGN_FAILED", "Failed to assign role: " + roleName);
        }
    }

    /**
     * Removes a realm role from a user.
     */
    public void removeRole(String userId, String roleName) {
        log.info("Removing role '{}' from user {}", roleName, userId);

        try {
            RealmResource realmResource = getRealmResource();

            // Verify user exists
            UserResource userResource = realmResource.users().get(userId);
            userResource.toRepresentation();

            // Get the realm role
            RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();

            // Remove the role
            userResource.roles().realmLevel().remove(Collections.singletonList(role));

            log.info("Role '{}' removed successfully from user {}", roleName, userId);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                log.error("User or role not found: userId={}, roleName={}", userId, roleName);
                throw new EntityNotFoundException("User or Role", userId + "/" + roleName);
            }
            log.error("Failed to remove role '{}' from user {}: {}", roleName, userId, e.getMessage());
            throw new BusinessException("ROLE_REMOVE_FAILED", "Failed to remove role: " + roleName);
        } catch (Exception e) {
            log.error("Failed to remove role '{}' from user {}: {}", roleName, userId, e.getMessage());
            throw new BusinessException("ROLE_REMOVE_FAILED", "Failed to remove role: " + roleName);
        }
    }

    /**
     * Gets all realm roles for a user.
     */
    public List<String> getUserRoles(String userId) {
        log.info("Fetching realm roles for user {}", userId);

        try {
            RealmResource realmResource = getRealmResource();

            UserResource userResource = realmResource.users().get(userId);
            List<RoleRepresentation> roles = userResource.roles().realmLevel().listAll();

            List<String> roleNames = roles.stream()
                    .map(RoleRepresentation::getName)
                    .collect(Collectors.toList());

            log.debug("Roles for user {}: {}", userId, roleNames);
            return roleNames;
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new EntityNotFoundException("User", userId);
            }
            log.error("Failed to fetch roles for user {}: {}", userId, e.getMessage());
            throw new BusinessException("ROLE_FETCH_FAILED", "Failed to fetch user roles");
        } catch (Exception e) {
            log.error("Failed to fetch roles for user {}: {}", userId, e.getMessage());
            throw new BusinessException("ROLE_FETCH_FAILED", "Failed to fetch user roles");
        }
    }

    /**
     * Creates a new realm role in Keycloak.
     */
    public void createRole(String roleName, String description) {
        log.info("Creating realm role '{}'", roleName);

        try {
            RealmResource realmResource = getRealmResource();

            // Check if role already exists
            try {
                realmResource.roles().get(roleName).toRepresentation();
                throw new BusinessException("ROLE_EXISTS", "Role already exists: " + roleName);
            } catch (WebApplicationException e) {
                if (e.getResponse().getStatus() != 404) {
                    throw e;
                }
                // 404 means role doesn't exist, which is what we want
            }

            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            role.setDescription(description);
            role.setComposite(false);

            realmResource.roles().create(role);

            log.info("Realm role '{}' created successfully", roleName);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to create role '{}': {}", roleName, e.getMessage());
            throw new BusinessException("ROLE_CREATE_FAILED", "Failed to create role: " + roleName);
        }
    }

    private RealmResource getRealmResource() {
        return keycloak.realm(keycloakProperties.getRealm());
    }
}
