package com.serenity.keycloak.service;

import com.serenity.keycloak.config.KeycloakProperties;
import com.serenity.keycloak.dto.GroupDto;
import com.serenity.keycloak.dto.UserDto;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private static final Logger log = LoggerFactory.getLogger(GroupService.class);

    private final Keycloak keycloak;
    private final KeycloakProperties properties;

    public GroupService(Keycloak keycloak, KeycloakProperties properties) {
        this.keycloak = keycloak;
        this.properties = properties;
    }

    private String realm() {
        return properties.getRealm();
    }

    /**
     * List all groups in the serenity realm.
     */
    public List<GroupDto> listGroups() {
        log.info("Listing groups in realm: {}", realm());
        return keycloak.realm(realm()).groups().groups().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new group.
     */
    public GroupDto createGroup(GroupDto groupDto) {
        log.info("Creating group: {} in realm: {}", groupDto.getName(), realm());
        GroupRepresentation groupRep = new GroupRepresentation();
        groupRep.setName(groupDto.getName());

        keycloak.realm(realm()).groups().add(groupRep);

        // Retrieve the created group
        List<GroupRepresentation> groups = keycloak.realm(realm()).groups().groups().stream()
                .filter(g -> g.getName().equals(groupDto.getName()))
                .collect(Collectors.toList());
        if (groups.isEmpty()) {
            throw new RuntimeException("Group was created but could not be retrieved");
        }
        return toDto(groups.get(0));
    }

    /**
     * Delete a group.
     */
    public void deleteGroup(String groupId) {
        log.info("Deleting group: {} in realm: {}", groupId, realm());
        keycloak.realm(realm()).groups().group(groupId).remove();
    }

    /**
     * Assign a user to a group.
     */
    public void assignUserToGroup(String groupId, String userId) {
        log.info("Assigning user {} to group {} in realm: {}", userId, groupId, realm());
        keycloak.realm(realm()).users().get(userId).joinGroup(groupId);
    }

    /**
     * Remove a user from a group.
     */
    public void removeUserFromGroup(String groupId, String userId) {
        log.info("Removing user {} from group {} in realm: {}", userId, groupId, realm());
        keycloak.realm(realm()).users().get(userId).leaveGroup(groupId);
    }

    /**
     * List members of a group.
     */
    public List<UserDto> listGroupMembers(String groupId) {
        log.info("Listing members of group {} in realm: {}", groupId, realm());
        return keycloak.realm(realm()).groups().group(groupId).members().stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }

    private GroupDto toDto(GroupRepresentation group) {
        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setPath(group.getPath());
        return dto;
    }

    private UserDto toUserDto(UserRepresentation user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.isEnabled());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setAttributes(user.getAttributes());
        return dto;
    }
}
