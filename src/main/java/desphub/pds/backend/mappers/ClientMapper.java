package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.ClientResponseDTO;
import desphub.pds.backend.dtos.CreateClientDTO;
import desphub.pds.backend.models.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    Client toEntity(CreateClientDTO dto);

    ClientResponseDTO toResponse(Client client);
}
