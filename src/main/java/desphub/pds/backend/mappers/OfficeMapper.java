package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.offices.CreateOfficeDTO;
import desphub.pds.backend.dtos.offices.OfficeResponseDTO;
import desphub.pds.backend.models.Office;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OfficeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Office toEntity(CreateOfficeDTO dto);

    OfficeResponseDTO toResponse(Office office);
}
