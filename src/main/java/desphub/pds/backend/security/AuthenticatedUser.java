package desphub.pds.backend.security;

import desphub.pds.backend.enums.Permission;
import desphub.pds.backend.enums.UserRole;

import java.util.Set;

public record AuthenticatedUser(
        Long userId,
        Long officeId,     // nulo para DESPHUB_ADMIN
        UserRole role,
        Set<Permission> permissions
) {
}
