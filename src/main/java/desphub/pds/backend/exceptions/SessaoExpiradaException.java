package desphub.pds.backend.exceptions;

// Pra erro 503
public class SessaoExpiradaException extends RuntimeException {
    public SessaoExpiradaException(String message) {
        super(message);
    }
}
