package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.users.UserResponseDTO;
import desphub.pds.backend.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "officeId", source = "office.id")
    UserResponseDTO toResponse(User user);
}
