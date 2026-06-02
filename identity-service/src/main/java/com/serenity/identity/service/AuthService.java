package com.serenity.identity.service;

import com.serenity.common.exception.BusinessException;
import com.serenity.common.exception.EntityNotFoundException;
import com.serenity.identity.config.KeycloakProperties;
import com.serenity.identity.dto.ChangePasswordRequest;
import com.serenity.identity.dto.LoginRequest;
import com.serenity.identity.dto.LoginResponse;
import com.serenity.identity.dto.RefreshRequest;
import com.serenity.identity.dto.RegisterRequest;
import com.serenity.identity.dto.UserInfoResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;
    private final KeycloakRoleService keycloakRoleService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Authenticates a user via Keycloak token endpoint and returns tokens with roles.
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        String tokenEndpoint = buildTokenEndpoint();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", request.getClientId());
        body.add("username", request.getUsername());
        body.add("password", request.getPassword());

        // For confidential clients, add the client secret
        String clientSecret = keycloakProperties.getClientSecrets() != null
                ? keycloakProperties.getClientSecrets().get(request.getClientId())
                : null;
        if (clientSecret != null) {
            body.add("client_secret", clientSecret);
        }

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            String responseBody = restTemplate.postForObject(tokenEndpoint, entity, String.class);
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            String accessToken = jsonNode.path("access_token").asText();
            String refreshToken = jsonNode.path("refresh_token").asText();
            long expiresIn = jsonNode.path("expires_in").asLong();
            String tokenType = jsonNode.path("token_type").asText();

            List<String> roles = extractRolesFromToken(accessToken);

            log.info("Login successful for user: {}", request.getUsername());
            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(expiresIn)
                    .tokenType(tokenType)
                    .roles(roles)
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Login failed for user: {} - {}", request.getUsername(), e.getMessage());
            throw new BusinessException("AUTH_FAILED", "Invalid username or password");
        }
    }

    /**
     * Refreshes an access token using a refresh token.
     */
    public LoginResponse refresh(RefreshRequest request) {
        log.info("Token refresh attempt for clientId: {}", request.getClientId());

        String tokenEndpoint = buildTokenEndpoint();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", request.getClientId());
        body.add("refresh_token", request.getRefreshToken());

        String clientSecret = keycloakProperties.getClientSecrets() != null
                ? keycloakProperties.getClientSecrets().get(request.getClientId())
                : null;
        if (clientSecret != null) {
            body.add("client_secret", clientSecret);
        }

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            String responseBody = restTemplate.postForObject(tokenEndpoint, entity, String.class);
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            String accessToken = jsonNode.path("access_token").asText();
            String refreshToken = jsonNode.path("refresh_token").asText();
            long expiresIn = jsonNode.path("expires_in").asLong();
            String tokenType = jsonNode.path("token_type").asText();

            List<String> roles = extractRolesFromToken(accessToken);

            log.info("Token refresh successful for clientId: {}", request.getClientId());
            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(expiresIn)
                    .tokenType(tokenType)
                    .roles(roles)
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new BusinessException("REFRESH_FAILED", "Invalid or expired refresh token");
        }
    }

    /**
     * Logs out a user by invalidating all sessions in Keycloak.
     */
    public void logout(String userId) {
        log.info("Logout attempt for userId: {}", userId);

        try {
            RealmResource realmResource = getRealmResource();
            UserResource userResource = realmResource.users().get(userId);
            userResource.logout();
            log.info("Logout successful for userId: {}", userId);
        } catch (WebApplicationException e) {
            log.error("Logout failed for userId: {} - Status: {}", userId, e.getResponse().getStatus());
            throw new BusinessException("LOGOUT_FAILED", "Failed to logout user");
        } catch (Exception e) {
            log.error("Logout failed for userId: {} - {}", userId, e.getMessage());
            throw new BusinessException("LOGOUT_FAILED", "Failed to logout user");
        }
    }

    /**
     * Gets the current user's information from Keycloak.
     */
    public UserInfoResponse getCurrentUser(String userId) {
        log.info("Fetching current user info for userId: {}", userId);

        UserRepresentation user = findUserById(userId);
        List<String> realmRoles = keycloakRoleService.getUserRoles(userId);

        // Separate composite roles (top-level roles like SUPER_ADMIN, ADMIN, MEMBRE, COLLECTEUR)
        // from fine-grained permission roles
        List<String> topLevelRoles = realmRoles.stream()
                .filter(role -> isTopLevelRole(role))
                .collect(Collectors.toList());

        return UserInfoResponse.builder()
                .id(UUID.fromString(user.getId()))
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(topLevelRoles)
                .realmRoles(realmRoles)
                .enabled(user.isEnabled())
                .build();
    }

    /**
     * Registers a new user in Keycloak and assigns the MEMBRE role.
     */
    public UserInfoResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Check if username already exists
        RealmResource realmResource = getRealmResource();
        List<UserRepresentation> existingUsers = realmResource.users()
                .searchByUsername(request.getUsername(), true);
        if (!existingUsers.isEmpty()) {
            throw new BusinessException("USERNAME_EXISTS", "Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        List<UserRepresentation> existingEmails = realmResource.users()
                .searchByEmail(request.getEmail(), true);
        if (!existingEmails.isEmpty()) {
            throw new BusinessException("EMAIL_EXISTS", "Email already exists: " + request.getEmail());
        }

        // Create user representation
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);
        user.setEmailVerified(false);

        // Set phone number as custom attribute if provided
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            user.setAttributes(Map.of("phoneNumber", List.of(request.getPhoneNumber())));
        }

        // Create user in Keycloak
        Response response;
        try {
            response = realmResource.users().create(user);
        } catch (Exception e) {
            log.error("Failed to create user in Keycloak: {}", e.getMessage());
            throw new BusinessException("REGISTRATION_FAILED", "Failed to create user: " + e.getMessage());
        }

        if (response.getStatus() != 201) {
            String errorMsg = "Failed to create user, status: " + response.getStatus();
            log.error(errorMsg);
            throw new BusinessException("REGISTRATION_FAILED", errorMsg);
        }

        // Extract created user ID from location header
        String locationHeader = response.getHeaderString("Location");
        String createdUserId = extractUserIdFromLocation(locationHeader);
        response.close();

        log.info("User created in Keycloak with ID: {}", createdUserId);

        // Set password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);

        try {
            realmResource.users().get(createdUserId).resetPassword(credential);
        } catch (Exception e) {
            log.error("Failed to set password for user {}: {}", createdUserId, e.getMessage());
            throw new BusinessException("REGISTRATION_FAILED", "User created but password setup failed");
        }

        // Assign MEMBRE role
        try {
            keycloakRoleService.assignRole(createdUserId, "MEMBRE");
        } catch (Exception e) {
            log.error("Failed to assign MEMBRE role to user {}: {}", createdUserId, e.getMessage());
            throw new BusinessException("REGISTRATION_FAILED", "User created but role assignment failed");
        }

        // Fetch the created user and return info
        UserRepresentation createdUser = realmResource.users().get(createdUserId).toRepresentation();
        List<String> realmRoles = keycloakRoleService.getUserRoles(createdUserId);

        log.info("User registration completed: {}", request.getUsername());

        return UserInfoResponse.builder()
                .id(UUID.fromString(createdUser.getId()))
                .username(createdUser.getUsername())
                .email(createdUser.getEmail())
                .firstName(createdUser.getFirstName())
                .lastName(createdUser.getLastName())
                .roles(List.of("MEMBRE"))
                .realmRoles(realmRoles)
                .enabled(createdUser.isEnabled())
                .build();
    }

    /**
     * Changes a user's password via Keycloak admin client.
     */
    public void changePassword(String userId, ChangePasswordRequest request) {
        log.info("Password change attempt for userId: {}", userId);

        // Verify the user exists
        findUserById(userId);

        // Verify current password by attempting a login with the current credentials
        // We use the admin-cli client to validate, but since we can't easily verify
        // the current password via admin API, we'll reset the password directly.
        // In a production system, you'd validate the current password first.
        UserRepresentation user = findUserById(userId);

        // Validate current password by attempting token acquisition
        boolean currentPasswordValid = validateCurrentPassword(
                user.getUsername(), request.getCurrentPassword());
        if (!currentPasswordValid) {
            throw new BusinessException("INVALID_CURRENT_PASSWORD", "Current password is incorrect");
        }

        // Set new password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getNewPassword());
        credential.setTemporary(false);

        try {
            getRealmResource().users().get(userId).resetPassword(credential);
            log.info("Password changed successfully for userId: {}", userId);
        } catch (Exception e) {
            log.error("Failed to change password for userId: {} - {}", userId, e.getMessage());
            throw new BusinessException("PASSWORD_CHANGE_FAILED", "Failed to change password");
        }
    }

    /**
     * Initiates a password reset by sending a reset email via Keycloak.
     */
    public void forgotPassword(String email) {
        log.info("Password reset requested for email: {}", email);

        RealmResource realmResource = getRealmResource();
        List<UserRepresentation> users = realmResource.users().searchByEmail(email, true);

        if (users.isEmpty()) {
            // Do not reveal whether the email exists for security reasons
            log.warn("Password reset requested for non-existent email: {}", email);
            return;
        }

        UserRepresentation user = users.get(0);
        try {
            realmResource.users().get(user.getId())
                    .executeActionsEmail(List.of("UPDATE_PASSWORD"));
            log.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {} - {}", email, e.getMessage());
            throw new BusinessException("RESET_EMAIL_FAILED", "Failed to send password reset email");
        }
    }

    // ==================== Helper methods ====================

    private String buildTokenEndpoint() {
        return keycloakProperties.getServerUrl()
                + "/realms/"
                + keycloakProperties.getRealm()
                + "/protocol/openid-connect/token";
    }

    private List<String> extractRolesFromToken(String accessToken) {
        try {
            String[] parts = accessToken.split("\\.");
            if (parts.length < 2) {
                log.warn("Invalid JWT token format");
                return Collections.emptyList();
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            JsonNode payloadNode = objectMapper.readTree(payload);

            JsonNode realmAccess = payloadNode.path("realm_access");
            if (realmAccess.isMissingNode() || !realmAccess.has("roles")) {
                log.debug("No realm_access.roles found in JWT token");
                return Collections.emptyList();
            }

            JsonNode rolesNode = realmAccess.get("roles");
            return objectMapper.convertValue(rolesNode, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            log.error("Failed to extract roles from token: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private UserRepresentation findUserById(String userId) {
        try {
            return getRealmResource().users().get(userId).toRepresentation();
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new EntityNotFoundException("User", userId);
            }
            log.error("Error fetching user {}: {}", userId, e.getMessage());
            throw new BusinessException("USER_FETCH_FAILED", "Failed to fetch user");
        } catch (Exception e) {
            log.error("Error fetching user {}: {}", userId, e.getMessage());
            throw new EntityNotFoundException("User", userId);
        }
    }

    private RealmResource getRealmResource() {
        return keycloak.realm(keycloakProperties.getRealm());
    }

    private String extractUserIdFromLocation(String location) {
        if (location == null) {
            throw new BusinessException("REGISTRATION_FAILED", "Failed to retrieve created user ID");
        }
        // Location header format: .../users/{userId}
        String[] parts = location.split("/");
        return parts[parts.length - 1];
    }

    private boolean isTopLevelRole(String role) {
        return role.equals("SUPER_ADMIN")
                || role.equals("ADMIN")
                || role.equals("COLLECTEUR")
                || role.equals("MEMBRE");
    }

    private boolean validateCurrentPassword(String username, String currentPassword) {
        String tokenEndpoint = buildTokenEndpoint();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", keycloakProperties.getAdminClientId());
        body.add("username", username);
        body.add("password", currentPassword);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            String response = restTemplate.postForObject(tokenEndpoint, entity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.has("access_token");
        } catch (Exception e) {
            log.debug("Current password validation failed for user: {}", username);
            return false;
        }
    }
}
