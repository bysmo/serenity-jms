package com.serenity.keycloak.controller;

import com.serenity.common.dto.ApiResponse;
import com.serenity.keycloak.dto.GroupDto;
import com.serenity.keycloak.dto.UserDto;
import com.serenity.keycloak.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/keycloak/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupDto>>> listGroups() {
        List<GroupDto> groups = groupService.listGroups();
        return ResponseEntity.ok(ApiResponse.success(groups));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GroupDto>> createGroup(@RequestBody GroupDto groupDto) {
        GroupDto created = groupService.createGroup(groupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Group created successfully", created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable String id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok(ApiResponse.success("Group deleted successfully", null));
    }

    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> assignUserToGroup(
            @PathVariable String id,
            @PathVariable String userId) {
        groupService.assignUserToGroup(id, userId);
        return ResponseEntity.ok(ApiResponse.success("User assigned to group successfully", null));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeUserFromGroup(
            @PathVariable String id,
            @PathVariable String userId) {
        groupService.removeUserFromGroup(id, userId);
        return ResponseEntity.ok(ApiResponse.success("User removed from group successfully", null));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<UserDto>>> listGroupMembers(@PathVariable String id) {
        List<UserDto> members = groupService.listGroupMembers(id);
        return ResponseEntity.ok(ApiResponse.success(members));
    }
}
