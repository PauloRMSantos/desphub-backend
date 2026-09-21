package desphub.pds.backend.services;

import desphub.pds.backend.dtos.detran.VehicleQueryResponse;
import desphub.pds.backend.exceptions.SessionExpiredException;
import desphub.pds.backend.integrations.RpaDetranClient;
import desphub.pds.backend.models.GovbrSession;
import desphub.pds.backend.repositories.IGovbrSessionRepository;
import desphub.pds.backend.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class VehicleQueryService {

    private final RpaDetranClient rpaDetranClient;
    private final IGovbrSessionRepository sessionRepository;
    private final CurrentUser currentUser;

    public VehicleQueryService(RpaDetranClient rpaDetranClient,
                               IGovbrSessionRepository sessionRepository,
                               CurrentUser currentUser) {
        this.rpaDetranClient = rpaDetranClient;
        this.sessionRepository = sessionRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public VehicleQueryResponse query(String plate, String renavam) {
        Long officeId = currentUser.requireOfficeId();
        GovbrSession session = sessionRepository.findById(officeId)
                .filter(this::isActive)
                .orElseThrow(() -> new SessionExpiredException(
                        "Conecte o gov.br do seu escritório para consultar o DETRAN"));
        return rpaDetranClient.query(plate, renavam, session.getBearer(), session.getUserId());
    }

    private boolean isActive(GovbrSession s) {
        return s.isConnected()
                && s.getBearer() != null
                && (s.getExpiresAt() == null || s.getExpiresAt().isAfter(Instant.now()));
    }
}
