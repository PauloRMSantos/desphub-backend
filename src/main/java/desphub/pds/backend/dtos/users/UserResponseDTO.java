package desphub.pds.backend.dtos.users;

import desphub.pds.backend.enums.Permission;
import desphub.pds.backend.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
public class UserResponseDTO {

    private Long id;
    private Long officeId;
    private String name;
    private String email;
    private UserRole role;
    private Set<Permission> permissions;
    private boolean active;
    private Instant createdAt;
}
