package desphub.pds.backend.services;

import desphub.pds.backend.dtos.CreateVehicleDTO;
import desphub.pds.backend.dtos.VehicleResponseDTO;
import desphub.pds.backend.mappers.VehicleMapper;
import desphub.pds.backend.models.Client;
import desphub.pds.backend.models.Vehicle;
import desphub.pds.backend.repositories.IClientRepository;
import desphub.pds.backend.repositories.IVehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public VehicleResponseDTO create(CreateVehicleDTO dto) {
        Vehicle vehicle = vehicleMapper.toEntity(dto);

        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Cliente não encontrado: " + dto.getClientId()));
            vehicle.setClient(client);
        }

        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(saved);
    }
}
