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
import desphub.pds.backend.security.CurrentUser;
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
    private final CurrentUser currentUser;

    public VehicleService(IVehicleRepository vehicleRepository,
                          IClientRepository clientRepository,
                          VehicleMapper vehicleMapper,
                          CurrentUser currentUser) {
        this.vehicleRepository = vehicleRepository;
        this.clientRepository = clientRepository;
        this.vehicleMapper = vehicleMapper;
        this.currentUser = currentUser;
    }

    @Transactional
    public VehicleResponseDTO create(CreateVehicleDTO dto) {
        Vehicle vehicle = vehicleMapper.toEntity(dto);
        vehicle.setOfficeId(currentUser.requireOfficeId());
        vehicle.setClient(resolveClient(dto.getClientId()));
        normalizeIdentifiers(vehicle);
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
        normalizeIdentifiers(vehicle);
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void delete(Long id) {
        vehicleRepository.delete(findEntityOr404(id));
    }

    // cliente do veículo tem que ser do mesmo escritório (opcional: null = sem dono)
    private Client resolveClient(Long clientId) {
        if (clientId == null) {
            return null;
        }
        return clientRepository.findByIdAndOfficeId(clientId, currentUser.requireOfficeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado: " + clientId));
    }

    private Vehicle findEntityOr404(Long id) {
        if (currentUser.isAdmin()) {
            return vehicleRepository.findById(id).orElseThrow(() -> notFound(id));
        }
        return vehicleRepository.findByIdAndOfficeId(id, currentUser.officeId()).orElseThrow(() -> notFound(id));
    }

    // placa/chassi em branco viram null; evita colisão de vazios nos índices únicos por escritório.
    // (0 km pode não ter placa; a exigência de chassi sem placa é validada no DTO)
    private void normalizeIdentifiers(Vehicle vehicle) {
        if (vehicle.getPlate() != null && vehicle.getPlate().isBlank()) {
            vehicle.setPlate(null);
        }
        if (vehicle.getChassis() != null && vehicle.getChassis().isBlank()) {
            vehicle.setChassis(null);
        }
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado: " + id);
    }
}
