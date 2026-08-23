package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.CreateServiceDTO;
import desphub.pds.backend.dtos.ServiceResponseDTO;
import desphub.pds.backend.models.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "id", ignore = true)
    Service toEntity(CreateServiceDTO dto);

    ServiceResponseDTO toResponse(Service service);
}
