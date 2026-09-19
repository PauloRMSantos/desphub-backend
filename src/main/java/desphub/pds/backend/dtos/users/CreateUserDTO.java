package desphub.pds.backend.dtos.users;

import desphub.pds.backend.enums.Permission;
import desphub.pds.backend.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class CreateUserDTO {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, message = "A senha deve ter ao menos 6 caracteres")
    private String password;

    @NotNull
    private UserRole role;

    // usar só pra role EMPLOYEE, admin e owner tem todas por padrão
    private Set<Permission> permissions = new HashSet<>();
}
