package desphub.pds.backend.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 6, message = "A senha deve ter ao menos 6 caracteres") String newPassword
) {
}
