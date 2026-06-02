package com.serenity.keycloak.service;

import com.serenity.keycloak.config.KeycloakProperties;
import com.serenity.keycloak.dto.ClientDto;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final Keycloak keycloak;
    private final KeycloakProperties properties;

    public ClientService(Keycloak keycloak, KeycloakProperties properties) {
        this.keycloak = keycloak;
        this.properties = properties;
    }

    private String realm() {
        return properties.getRealm();
    }

    /**
     * List all clients in the serenity realm.
     */
    public List<ClientDto> listClients() {
        log.info("Listing clients in realm: {}", realm());
        return keycloak.realm(realm()).clients().findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new client in the serenity realm.
     */
    public ClientDto createClient(ClientDto clientDto) {
        log.info("Creating client: {} in realm: {}", clientDto.getClientId(), realm());
        ClientRepresentation clientRep = new ClientRepresentation();
        clientRep.setClientId(clientDto.getClientId());
        clientRep.setName(clientDto.getName());
        clientRep.setDescription(clientDto.getDescription());
        clientRep.setEnabled(clientDto.getEnabled() != null ? clientDto.getEnabled() : true);
        clientRep.setPublicClient(clientDto.getPublicClient() != null ? clientDto.getPublicClient() : false);
        clientRep.setDirectAccessGrantsEnabled(
                clientDto.getDirectAccessGrantsEnabled() != null ? clientDto.getDirectAccessGrantsEnabled() : true);
        clientRep.setRedirectUris(clientDto.getRedirectUris());
        clientRep.setWebOrigins(clientDto.getWebOrigins());

        keycloak.realm(realm()).clients().create(clientRep);

        // Retrieve the created client
        List<ClientRepresentation> clients = keycloak.realm(realm()).clients()
                .findByClientId(clientDto.getClientId());
        if (clients.isEmpty()) {
            throw new RuntimeException("Client was created but could not be retrieved");
        }
        return toDto(clients.get(0));
    }

    /**
     * Update a client.
     */
    public ClientDto updateClient(String clientId, ClientDto clientDto) {
        log.info("Updating client with ID: {} in realm: {}", clientId, realm());
        ClientRepresentation clientRep = keycloak.realm(realm()).clients().get(clientId).toRepresentation();

        if (clientDto.getClientId() != null) clientRep.setClientId(clientDto.getClientId());
        if (clientDto.getName() != null) clientRep.setName(clientDto.getName());
        if (clientDto.getDescription() != null) clientRep.setDescription(clientDto.getDescription());
        if (clientDto.getEnabled() != null) clientRep.setEnabled(clientDto.getEnabled());
        if (clientDto.getPublicClient() != null) clientRep.setPublicClient(clientDto.getPublicClient());
        if (clientDto.getRedirectUris() != null) clientRep.setRedirectUris(clientDto.getRedirectUris());
        if (clientDto.getWebOrigins() != null) clientRep.setWebOrigins(clientDto.getWebOrigins());

        keycloak.realm(realm()).clients().get(clientId).update(clientRep);

        return toDto(keycloak.realm(realm()).clients().get(clientId).toRepresentation());
    }

    /**
     * Delete a client.
     */
    public void deleteClient(String clientId) {
        log.info("Deleting client with ID: {} in realm: {}", clientId, realm());
        keycloak.realm(realm()).clients().get(clientId).remove();
    }

    private ClientDto toDto(ClientRepresentation client) {
        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setClientId(client.getClientId());
        dto.setName(client.getName());
        dto.setDescription(client.getDescription());
        dto.setEnabled(client.isEnabled());
        dto.setPublicClient(client.isPublicClient());
        dto.setDirectAccessGrantsEnabled(client.isDirectAccessGrantsEnabled());
        dto.setRedirectUris(client.getRedirectUris());
        dto.setWebOrigins(client.getWebOrigins());
        return dto;
    }
}
