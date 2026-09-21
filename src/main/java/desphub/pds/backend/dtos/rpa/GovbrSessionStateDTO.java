package desphub.pds.backend.dtos.rpa;

import java.time.Instant;

public record GovbrSessionStateDTO(boolean connected, Instant expiresAt) {
}
