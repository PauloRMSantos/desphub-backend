package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.clients.ClientGetResponseDTO;
import desphub.pds.backend.dtos.clients.ClientResponseDTO;
import desphub.pds.backend.dtos.clients.CreateClientDTO;
import desphub.pds.backend.dtos.clients.UpdateClientDTO;
import desphub.pds.backend.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENTS_WRITE')")
    public ResponseEntity<ClientResponseDTO> create(@Valid @RequestBody CreateClientDTO dto) {
        ClientResponseDTO created = clientService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENTS_READ')")
    public ResponseEntity<List<ClientGetResponseDTO>> findAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTS_READ')")
    public ResponseEntity<ClientGetResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTS_WRITE')")
    public ResponseEntity<ClientResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateClientDTO dto) {
        return ResponseEntity.ok(clientService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTS_WRITE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
