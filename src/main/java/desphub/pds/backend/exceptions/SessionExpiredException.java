package desphub.pds.backend.exceptions;

// Para o erro 503 do RPA
public class SessionExpiredException extends RuntimeException {
    public SessionExpiredException(String message) {
        super(message);
    }
}
