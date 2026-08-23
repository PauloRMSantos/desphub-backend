package desphub.pds.backend.services;

import desphub.pds.backend.dtos.ClientResponseDTO;
import desphub.pds.backend.dtos.CreateClientDTO;
import desphub.pds.backend.mappers.ClientMapper;
import desphub.pds.backend.models.Client;
import desphub.pds.backend.repositories.IClientRepository;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final IClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientService(IClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public ClientResponseDTO create(CreateClientDTO dto) {
        Client client = clientMapper.toEntity(dto);
        Client saved = clientRepository.save(client);
        return clientMapper.toResponse(saved);
    }
}
