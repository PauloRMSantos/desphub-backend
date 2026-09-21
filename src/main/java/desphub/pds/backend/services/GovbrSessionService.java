package desphub.pds.backend.services;

import desphub.pds.backend.dtos.rpa.GovbrSessionResponse;
import desphub.pds.backend.dtos.rpa.GovbrSessionStateDTO;
import desphub.pds.backend.models.GovbrSession;
import desphub.pds.backend.repositories.IGovbrSessionRepository;
import desphub.pds.backend.security.CurrentUser;
import desphub.pds.backend.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GovbrSessionService {

    private static final Pattern EXP = Pattern.compile("\"exp\"\\s*:\\s*(\\d+)");

    private final JwtService jwtService;
    private final IGovbrSessionRepository sessionRepository;
    private final CurrentUser currentUser;

    public GovbrSessionService(JwtService jwtService,
                               IGovbrSessionRepository sessionRepository,
                               CurrentUser currentUser) {
        this.jwtService = jwtService;
        this.sessionRepository = sessionRepository;
        this.currentUser = currentUser;
    }

    public String issuePairingToken() {
        return jwtService.generatePairingToken(currentUser.get().userId(), currentUser.requireOfficeId());
    }

    @Transactional
    public GovbrSessionResponse relay(String pairingToken, String bearer, String userId) {
        Long officeId = validatePairing(pairingToken);

        Instant expiresAt = extractExp(bearer);
        GovbrSession session = sessionRepository.findById(officeId).orElseGet(() -> {
            GovbrSession novo = new GovbrSession();
            novo.setOfficeId(officeId);
            return novo;
        });
        session.setConnected(true);
        session.setExpiresAt(expiresAt);
        session.setBearer(bearer);   // criptografado pelo converter
        session.setUserId(userId);   // criptografado pelo converter
        sessionRepository.save(session);

        return new GovbrSessionResponse("connected", expiresAt);
    }

    /** Estado atual da conexão do escritório (para o badge). */
    @Transactional(readOnly = true)
    public GovbrSessionStateDTO state() {
        Long officeId = currentUser.requireOfficeId();
        return sessionRepository.findById(officeId)
                .map(s -> new GovbrSessionStateDTO(isActive(s), s.getExpiresAt()))
                .orElse(new GovbrSessionStateDTO(false, null));
    }

    private boolean isActive(GovbrSession s) {
        return s.isConnected() && (s.getExpiresAt() == null || s.getExpiresAt().isAfter(Instant.now()));
    }

    private Long validatePairing(String pairingToken) {
        try {
            Claims claims = jwtService.parse(pairingToken);
            if (!JwtService.TYPE_PAIRING.equals(claims.get("type", String.class))) {
                throw unauthorized();
            }
            Number officeId = claims.get("officeId", Number.class);
            if (officeId == null) {
                throw unauthorized();
            }
            return officeId.longValue();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw unauthorized(); // assinatura inválida / expirado / malformado
        }
    }

    private Instant extractExp(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) {
                return null;
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Matcher matcher = EXP.matcher(payload);
            return matcher.find() ? Instant.ofEpochSecond(Long.parseLong(matcher.group(1))) : null;
        } catch (Exception e) {
            return null; // sem exp legível: guardamos sem expiração
        }
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de pareamento inválido ou expirado");
    }
}
