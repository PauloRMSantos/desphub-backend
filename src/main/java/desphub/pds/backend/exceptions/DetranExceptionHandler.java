package desphub.pds.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class DetranExceptionHandler {

    @ExceptionHandler(SessaoExpiradaException.class)
    public ResponseEntity<IntegracaoErroDTO> handleSessaoExpirada(SessaoExpiradaException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new IntegracaoErroDTO("RECONECTAR", ex.getMessage()));
    }

    @ExceptionHandler(PortalException.class)
    public ResponseEntity<Object> handlePortal(PortalException ex) {
        Object body = ex.getResposta() != null
                ? ex.getResposta()
                : new IntegracaoErroDTO("ERRO", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<IntegracaoErroDTO> handleRpaIndisponivel(ResourceAccessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new IntegracaoErroDTO("RPA_INDISPONIVEL",
                        "Serviço de consulta veicular indisponível no momento"));
    }
}
