package desphub.pds.backend.dtos.users;

import desphub.pds.backend.enums.Permission;

import java.util.HashSet;
import java.util.Set;

public record UpdatePermissionsRequest(Set<Permission> permissions) {

    public UpdatePermissionsRequest {
        permissions = permissions == null ? new HashSet<>() : permissions;
    }
}
