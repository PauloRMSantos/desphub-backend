package desphub.pds.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class DetranExceptionHandler {

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<IntegrationErrorDTO> handleSessionExpired(SessionExpiredException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new IntegrationErrorDTO("RECONNECT", ex.getMessage()));
    }

    @ExceptionHandler(PortalException.class)
    public ResponseEntity<Object> handlePortal(PortalException ex) {
        Object body = ex.getResponse() != null
                ? ex.getResponse()
                : new IntegrationErrorDTO("ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<IntegrationErrorDTO> handleRpaUnavailable(ResourceAccessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new IntegrationErrorDTO("RPA_UNAVAILABLE",
                        "Serviço de consulta veicular indisponível no momento"));
    }
}
