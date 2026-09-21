package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.rpa.GovbrSessionRequest;
import desphub.pds.backend.dtos.rpa.GovbrSessionResponse;
import desphub.pds.backend.dtos.rpa.GovbrSessionStateDTO;
import desphub.pds.backend.dtos.rpa.PairingTokenResponse;
import desphub.pds.backend.services.GovbrSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/rpa")
public class RpaSessionController {

    private final GovbrSessionService govbrSessionService;

    public RpaSessionController(GovbrSessionService govbrSessionService) {
        this.govbrSessionService = govbrSessionService;
    }

    @PostMapping("/pairing-token")
    @PreAuthorize("hasAuthority('VEHICLE_QUERY')")
    public ResponseEntity<PairingTokenResponse> pairingToken() {
        return ResponseEntity.ok(new PairingTokenResponse(govbrSessionService.issuePairingToken()));
    }

    @PostMapping("/govbr-session")
    public ResponseEntity<GovbrSessionResponse> govbrSession(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody GovbrSessionRequest request) {
        String pairingToken = extractBearer(authorization);
        return ResponseEntity.ok(govbrSessionService.relay(pairingToken, request.bearer(), request.userId()));
    }

    @GetMapping("/session")
    @PreAuthorize("hasAuthority('VEHICLE_QUERY')")
    public ResponseEntity<GovbrSessionStateDTO> session() {
        return ResponseEntity.ok(govbrSessionService.state());
    }

    private String extractBearer(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falta o pairing token no header Authorization");
        }
        return authorization.substring(7);
    }
}
