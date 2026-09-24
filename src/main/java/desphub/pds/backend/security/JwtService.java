package desphub.pds.backend.security;

import desphub.pds.backend.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    public static final String TYPE_PAIRING = "pairing";
    public static final String TYPE_PASSWORD_RESET = "password_reset";

    private final SecretKey key;
    private final long expirationMs;
    private final long pairingExpirationMs;
    private final long passwordResetExpirationMs;

    public JwtService(@Value("${desphub.jwt.secret}") String secret,
                      @Value("${desphub.jwt.expiration-minutes}") long expirationMinutes,
                      @Value("${desphub.jwt.pairing-expiration-minutes:5}") long pairingExpirationMinutes,
                      @Value("${desphub.jwt.password-reset-expiration-minutes:30}") long passwordResetExpirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMinutes * 60_000L;
        this.pairingExpirationMs = pairingExpirationMinutes * 60_000L;
        this.passwordResetExpirationMs = passwordResetExpirationMinutes * 60_000L;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("officeId", user.getOffice() != null ? user.getOffice().getId() : null)
                .claim("role", user.getRole().name())
                .claim("permissions", user.getPermissions().stream().map(Enum::name).toList())
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key)
                .compact();
    }

    public String generatePairingToken(Long userId, Long officeId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("officeId", officeId)
                .claim("type", TYPE_PAIRING)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(pairingExpirationMs)))
                .signWith(key)
                .compact();
    }

    public String generatePasswordResetToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("type", TYPE_PASSWORD_RESET)
                .claim("pwv", user.getPasswordHash().hashCode())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(passwordResetExpirationMs)))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000L;
    }
}
