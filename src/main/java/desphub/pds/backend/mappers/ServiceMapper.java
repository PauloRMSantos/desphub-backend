package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.services.CreateServiceDTO;
import desphub.pds.backend.dtos.services.ServiceGetResponseDTO;
import desphub.pds.backend.dtos.services.ServiceResponseDTO;
import desphub.pds.backend.dtos.services.UpdateServiceDTO;
import desphub.pds.backend.models.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "id", ignore = true)
    Service toEntity(CreateServiceDTO dto);

    ServiceResponseDTO toResponse(Service service);

    ServiceGetResponseDTO toGetResponse(Service service);

    @Mapping(target = "id", ignore = true)
    void updateEntity(UpdateServiceDTO dto, @MappingTarget Service entity);
}
