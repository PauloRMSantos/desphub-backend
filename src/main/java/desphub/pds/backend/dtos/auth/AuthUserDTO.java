package desphub.pds.backend.dtos.auth;

import desphub.pds.backend.enums.Permission;
import desphub.pds.backend.enums.UserRole;

import java.util.Set;

public record AuthUserDTO(
        Long userId,
        String name,
        String email,
        UserRole role,
        Long officeId,
        Set<Permission> permissions
) {
}
