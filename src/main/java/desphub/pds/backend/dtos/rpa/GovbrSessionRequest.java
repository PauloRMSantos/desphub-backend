package desphub.pds.backend.dtos.rpa;

import jakarta.validation.constraints.NotBlank;

public record GovbrSessionRequest(
        @NotBlank String bearer,   // JWT gov.br
        @NotBlank String userId    // X-User-Id (CPF em base64)
) {
}
