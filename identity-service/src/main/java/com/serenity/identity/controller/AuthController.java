package com.serenity.identity.controller;

import com.serenity.common.dto.ApiResponse;
import com.serenity.common.security.SecurityUtils;
import com.serenity.identity.dto.ChangePasswordRequest;
import com.serenity.identity.dto.LoginRequest;
import com.serenity.identity.dto.LoginResponse;
import com.serenity.identity.dto.RefreshRequest;
import com.serenity.identity.dto.RegisterRequest;
import com.serenity.identity.dto.UserInfoResponse;
import com.serenity.identity.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates a user via Keycloak and returns JWT tokens with roles")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for user: {}", request.getUsername());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refreshes an access token using a refresh token")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        log.info("Token refresh request received");
        LoginResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidates the user's Keycloak session")
    public ResponseEntity<ApiResponse<Void>> logout() {
        UUID userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.success("No active session", null));
        }
        log.info("Logout request received for userId: {}", userId);
        authService.logout(userId.toString());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns the current authenticated user's information from Keycloak")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUser() {
        UUID userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Not authenticated"));
        }
        log.info("Get current user request for userId: {}", userId);
        UserInfoResponse response = authService.getCurrentUser(userId.toString());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Registers a new user in Keycloak with the MEMBRE role")
    public ResponseEntity<ApiResponse<UserInfoResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received for user: {}", request.getUsername());
        UserInfoResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.created("User registered successfully", response));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Changes the current user's password via Keycloak API")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Not authenticated"));
        }
        log.info("Password change request for userId: {}", userId);
        authService.changePassword(userId.toString(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Initiates a password reset by sending a reset email via Keycloak")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        log.info("Forgot password request for email: {}", email);
        authService.forgotPassword(email);
        // Always return success to prevent email enumeration
        return ResponseEntity.ok(ApiResponse.success(
                "If an account exists with this email, a password reset link has been sent", null));
    }
}
