package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.vehicles.CreateVehicleDTO;
import desphub.pds.backend.dtos.vehicles.UpdateVehicleDTO;
import desphub.pds.backend.dtos.vehicles.VehicleGetResponseDTO;
import desphub.pds.backend.dtos.vehicles.VehicleResponseDTO;
import desphub.pds.backend.models.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    // id gerado pelo banco; client é resolvido no service a partir do clientId
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    Vehicle toEntity(CreateVehicleDTO dto);

    @Mapping(target = "clientId", source = "client.id")
    VehicleResponseDTO toResponse(Vehicle vehicle);

    @Mapping(target = "clientId", source = "client.id")
    VehicleGetResponseDTO toGetResponse(Vehicle vehicle);

    // client é resolvido no service; id nunca muda
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    void updateEntity(UpdateVehicleDTO dto, @MappingTarget Vehicle entity);
}
