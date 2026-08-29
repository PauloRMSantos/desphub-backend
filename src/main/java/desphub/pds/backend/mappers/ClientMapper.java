package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.clients.ClientGetResponseDTO;
import desphub.pds.backend.dtos.clients.ClientResponseDTO;
import desphub.pds.backend.dtos.clients.CreateClientDTO;
import desphub.pds.backend.dtos.clients.UpdateClientDTO;
import desphub.pds.backend.models.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    Client toEntity(CreateClientDTO dto);

    ClientResponseDTO toResponse(Client client);

    ClientGetResponseDTO toGetResponse(Client client);

    // copia os campos do DTO por cima de uma entidade já existente (update)
    @Mapping(target = "id", ignore = true)
    void updateEntity(UpdateClientDTO dto, @MappingTarget Client entity);
}
