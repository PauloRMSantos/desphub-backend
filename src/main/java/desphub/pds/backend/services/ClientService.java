package desphub.pds.backend.services;

import desphub.pds.backend.dtos.clients.ClientGetResponseDTO;
import desphub.pds.backend.dtos.clients.ClientResponseDTO;
import desphub.pds.backend.dtos.clients.CreateClientDTO;
import desphub.pds.backend.dtos.clients.UpdateClientDTO;
import desphub.pds.backend.mappers.ClientMapper;
import desphub.pds.backend.models.Client;
import desphub.pds.backend.repositories.IClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClientService {

    private final IClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientService(IClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Transactional
    public ClientResponseDTO create(CreateClientDTO dto) {
        Client client = clientMapper.toEntity(dto);
        Client saved = clientRepository.save(client);
        return clientMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ClientGetResponseDTO> findAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toGetResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientGetResponseDTO findById(Long id) {
        Client client = findEntityOr404(id);
        return clientMapper.toGetResponse(client);
    }

    @Transactional
    public ClientResponseDTO update(Long id, UpdateClientDTO dto) {
        Client client = findEntityOr404(id);
        clientMapper.updateEntity(dto, client);
        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Transactional
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw notFound(id);
        }
        clientRepository.deleteById(id);
    }

    private Client findEntityOr404(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> notFound(id));
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado: " + id);
    }
}
