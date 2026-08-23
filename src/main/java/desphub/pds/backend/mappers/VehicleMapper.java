package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.CreateVehicleDTO;
import desphub.pds.backend.dtos.VehicleResponseDTO;
import desphub.pds.backend.models.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    Vehicle toEntity(CreateVehicleDTO dto);

    @Mapping(target = "clientId", source = "client.id")
    VehicleResponseDTO toResponse(Vehicle vehicle);
}
