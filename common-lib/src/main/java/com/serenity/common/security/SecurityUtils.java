package com.serenity.common.security;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@UtilityClass
public class SecurityUtils {

    private static final String SUB_CLAIM = "sub";
    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * Gets the current authenticated user's ID from the JWT subject claim.
     *
     * @return the user ID as a UUID, or null if no authentication is present
     */
    public UUID getCurrentUserId() {
        Jwt jwt = getCurrentJwt();
        if (jwt == null) {
            log.warn("No JWT token found in security context");
            return null;
        }
        String subject = jwt.getClaimAsString(SUB_CLAIM);
        if (subject == null) {
            log.warn("No 'sub' claim found in JWT token");
            return null;
        }
        return UUID.fromString(subject);
    }

    /**
     * Gets the current authenticated user's roles from the security context.
     *
     * @return a collection of role strings (without the ROLE_ prefix), or empty collection if no authentication
     */
    public Collection<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return Collections.emptyList();
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> {
                    if (authority.startsWith(ROLE_PREFIX)) {
                        return authority.substring(ROLE_PREFIX.length());
                    }
                    return authority;
                })
                .toList();
    }

    /**
     * Checks if the current authenticated user has the specified role.
     *
     * @param role the role to check (without the ROLE_ prefix)
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        Collection<String> roles = getCurrentUserRoles();
        return roles.contains(role);
    }

    /**
     * Gets the current JWT token from the security context.
     *
     * @return the Jwt token, or null if not available
     */
    public Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt;
        }
        return null;
    }

    /**
     * Checks if a user is currently authenticated.
     *
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String);
    }
}
