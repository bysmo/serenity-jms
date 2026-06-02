package com.serenity.keycloak.controller;

import com.serenity.common.dto.ApiResponse;
import com.serenity.keycloak.dto.RoleDto;
import com.serenity.keycloak.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/keycloak/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleDto>>> listRoles() {
        List<RoleDto> roles = roleService.listRoles();
        return ResponseEntity.ok(ApiResponse.success(roles));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleDto>> createRole(@RequestBody RoleDto roleDto) {
        RoleDto created = roleService.createRole(roleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Role created successfully", created));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable String name) {
        roleService.deleteRole(name);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully", null));
    }

    @PostMapping("/{name}/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> assignRoleToUser(
            @PathVariable String name,
            @PathVariable String userId) {
        roleService.assignRoleToUser(name, userId);
        return ResponseEntity.ok(ApiResponse.success("Role assigned to user successfully", null));
    }

    @DeleteMapping("/{name}/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeRoleFromUser(
            @PathVariable String name,
            @PathVariable String userId) {
        roleService.removeRoleFromUser(name, userId);
        return ResponseEntity.ok(ApiResponse.success("Role removed from user successfully", null));
    }
}
