package desphub.pds.backend.security;

import desphub.pds.backend.enums.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CurrentUser {

    public AuthenticatedUser get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado");
        }
        return user;
    }

    public Long officeId() {
        return get().officeId();
    }

    public Long requireOfficeId() {
        Long officeId = officeId();
        if (officeId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta ação exige um escritório vinculado");
        }
        return officeId;
    }

    public boolean isAdmin() {
        return get().role() == UserRole.DESPHUB_ADMIN;
    }
}
