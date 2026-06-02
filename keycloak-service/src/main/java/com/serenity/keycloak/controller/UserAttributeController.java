package com.serenity.keycloak.controller;

import com.serenity.common.dto.ApiResponse;
import com.serenity.keycloak.dto.UserAttributeDto;
import com.serenity.keycloak.service.UserAttributeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/keycloak/users/{userId}/attributes")
public class UserAttributeController {

    private final UserAttributeService userAttributeService;

    public UserAttributeController(UserAttributeService userAttributeService) {
        this.userAttributeService = userAttributeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserAttributeDto>> getUserAttributes(@PathVariable String userId) {
        UserAttributeDto attributes = userAttributeService.getUserAttributes(userId);
        return ResponseEntity.ok(ApiResponse.success(attributes));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserAttributeDto>> updateUserAttributes(
            @PathVariable String userId,
            @RequestBody UserAttributeDto attributeDto) {
        UserAttributeDto updated = userAttributeService.updateUserAttributes(userId, attributeDto);
        return ResponseEntity.ok(ApiResponse.success("User attributes updated successfully", updated));
    }
}
