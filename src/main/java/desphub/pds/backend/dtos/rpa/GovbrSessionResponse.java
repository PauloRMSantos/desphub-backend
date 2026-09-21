package desphub.pds.backend.dtos.rpa;

import java.time.Instant;

public record GovbrSessionResponse(String status, Instant expiresAt) {
}
