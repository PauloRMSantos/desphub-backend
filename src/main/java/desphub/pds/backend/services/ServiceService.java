package desphub.pds.backend.services;

import desphub.pds.backend.dtos.services.CreateServiceDTO;
import desphub.pds.backend.dtos.services.ServiceGetResponseDTO;
import desphub.pds.backend.dtos.services.ServiceResponseDTO;
import desphub.pds.backend.dtos.services.UpdateServiceDTO;
import desphub.pds.backend.mappers.ServiceMapper;
import desphub.pds.backend.models.Service;
import desphub.pds.backend.repositories.IServiceRepository;
import desphub.pds.backend.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// A entidade "Service" colide com a anotação @Service do Spring; por isso qualificada.
@org.springframework.stereotype.Service
public class ServiceService {

    private final IServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final CurrentUser currentUser;

    public ServiceService(IServiceRepository serviceRepository, ServiceMapper serviceMapper, CurrentUser currentUser) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
        this.currentUser = currentUser;
    }

    @Transactional
    public ServiceResponseDTO create(CreateServiceDTO dto) {
        Service service = serviceMapper.toEntity(dto);
        service.setOfficeId(currentUser.requireOfficeId());
        return serviceMapper.toResponse(serviceRepository.save(service));
    }

    @Transactional(readOnly = true)
    public List<ServiceGetResponseDTO> findAll() {
        return serviceRepository.findAll().stream()
                .map(serviceMapper::toGetResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceGetResponseDTO findById(Long id) {
        return serviceMapper.toGetResponse(findEntityOr404(id));
    }

    @Transactional
    public ServiceResponseDTO update(Long id, UpdateServiceDTO dto) {
        Service service = findEntityOr404(id);
        serviceMapper.updateEntity(dto, service);
        return serviceMapper.toResponse(serviceRepository.save(service));
    }

    @Transactional
    public void delete(Long id) {
        serviceRepository.delete(findEntityOr404(id));
    }

    private Service findEntityOr404(Long id) {
        if (currentUser.isAdmin()) {
            return serviceRepository.findById(id).orElseThrow(() -> notFound(id));
        }
        return serviceRepository.findByIdAndOfficeId(id, currentUser.officeId()).orElseThrow(() -> notFound(id));
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado: " + id);
    }
}
