package com.serenity.keycloak.controller;

import com.serenity.common.dto.ApiResponse;
import com.serenity.keycloak.dto.ClientDto;
import com.serenity.keycloak.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/keycloak/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientDto>>> listClients() {
        List<ClientDto> clients = clientService.listClients();
        return ResponseEntity.ok(ApiResponse.success(clients));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClientDto>> createClient(@RequestBody ClientDto clientDto) {
        ClientDto created = clientService.createClient(clientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Client created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientDto>> updateClient(
            @PathVariable String id,
            @RequestBody ClientDto clientDto) {
        ClientDto updated = clientService.updateClient(id, clientDto);
        return ResponseEntity.ok(ApiResponse.success("Client updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable String id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok(ApiResponse.success("Client deleted successfully", null));
    }
}
