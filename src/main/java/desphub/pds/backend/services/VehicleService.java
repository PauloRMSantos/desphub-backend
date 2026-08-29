package desphub.pds.backend.services;

import desphub.pds.backend.dtos.vehicles.CreateVehicleDTO;
import desphub.pds.backend.dtos.vehicles.UpdateVehicleDTO;
import desphub.pds.backend.dtos.vehicles.VehicleGetResponseDTO;
import desphub.pds.backend.dtos.vehicles.VehicleResponseDTO;
import desphub.pds.backend.mappers.VehicleMapper;
import desphub.pds.backend.models.Client;
import desphub.pds.backend.models.Vehicle;
import desphub.pds.backend.repositories.IClientRepository;
import desphub.pds.backend.repositories.IVehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VehicleService {

    private final IVehicleRepository vehicleRepository;
    private final IClientRepository clientRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(IVehicleRepository vehicleRepository,
                          IClientRepository clientRepository,
                          VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.clientRepository = clientRepository;
        this.vehicleMapper = vehicleMapper;
    }

    @Transactional
    public VehicleResponseDTO create(CreateVehicleDTO dto) {
        Vehicle vehicle = vehicleMapper.toEntity(dto);
        vehicle.setClient(resolveClient(dto.getClientId()));
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional(readOnly = true)
    public List<VehicleGetResponseDTO> findAll() {
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toGetResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehicleGetResponseDTO findById(Long id) {
        return vehicleMapper.toGetResponse(findEntityOr404(id));
    }

    @Transactional
    public VehicleResponseDTO update(Long id, UpdateVehicleDTO dto) {
        Vehicle vehicle = findEntityOr404(id);
        vehicleMapper.updateEntity(dto, vehicle);
        vehicle.setClient(resolveClient(dto.getClientId()));
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw notFound(id);
        }
        vehicleRepository.deleteById(id);
    }

    // clientId é opcional: null significa veículo sem dono
    private Client resolveClient(Long clientId) {
        if (clientId == null) {
            return null;
        }
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado: " + clientId));
    }

    private Vehicle findEntityOr404(Long id) {
        return vehicleRepository.findById(id).orElseThrow(() -> notFound(id));
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado: " + id);
    }
}
