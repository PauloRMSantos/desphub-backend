package desphub.pds.backend.services;

import desphub.pds.backend.dtos.clients.ClientGetResponseDTO;
import desphub.pds.backend.dtos.clients.ClientResponseDTO;
import desphub.pds.backend.dtos.clients.CreateClientDTO;
import desphub.pds.backend.dtos.clients.UpdateClientDTO;
import desphub.pds.backend.mappers.ClientMapper;
import desphub.pds.backend.models.Client;
import desphub.pds.backend.repositories.IClientRepository;
import desphub.pds.backend.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClientService {

    private final IClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final CurrentUser currentUser;

    public ClientService(IClientRepository clientRepository, ClientMapper clientMapper, CurrentUser currentUser) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.currentUser = currentUser;
    }

    @Transactional
    public ClientResponseDTO create(CreateClientDTO dto) {
        Client client = clientMapper.toEntity(dto);
        client.setOfficeId(currentUser.requireOfficeId());
        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Transactional(readOnly = true)
    public List<ClientGetResponseDTO> findAll() {
        // leitura isolada por escritório pelo officeFilter (TenantFilterAspect)
        return clientRepository.findAll().stream()
                .map(clientMapper::toGetResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientGetResponseDTO findById(Long id) {
        return clientMapper.toGetResponse(findEntityOr404(id));
    }

    @Transactional
    public ClientResponseDTO update(Long id, UpdateClientDTO dto) {
        Client client = findEntityOr404(id);
        clientMapper.updateEntity(dto, client);
        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Transactional
    public void delete(Long id) {
        clientRepository.delete(findEntityOr404(id));
    }

    // find-by-id não é coberto pelo @Filter: filtra por escritório explicitamente
    private Client findEntityOr404(Long id) {
        if (currentUser.isAdmin()) {
            return clientRepository.findById(id).orElseThrow(() -> notFound(id));
        }
        return clientRepository.findByIdAndOfficeId(id, currentUser.officeId()).orElseThrow(() -> notFound(id));
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado: " + id);
    }
}
