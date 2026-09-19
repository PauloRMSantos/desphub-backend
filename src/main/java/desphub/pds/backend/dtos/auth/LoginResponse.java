package desphub.pds.backend.dtos.auth;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInSeconds,
        AuthUserDTO user
) {
}
