package desphub.pds.backend.services;

import desphub.pds.backend.dtos.CreateServiceDTO;
import desphub.pds.backend.dtos.ServiceResponseDTO;
import desphub.pds.backend.mappers.ServiceMapper;
import desphub.pds.backend.models.Service;
import desphub.pds.backend.repositories.IServiceRepository;

// A entidade "Service" colide com a anotação do Spring
// Por isso a anotação abaixo tá totalmente qualificada
@org.springframework.stereotype.Service
public class ServiceService {

    private final IServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    public ServiceService(IServiceRepository serviceRepository, ServiceMapper serviceMapper) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
    }

    public ServiceResponseDTO create(CreateServiceDTO dto) {
        Service service = serviceMapper.toEntity(dto);
        Service saved = serviceRepository.save(service);
        return serviceMapper.toResponse(saved);
    }
}
