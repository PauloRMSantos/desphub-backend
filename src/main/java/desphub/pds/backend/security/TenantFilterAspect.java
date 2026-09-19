package desphub.pds.backend.security;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class TenantFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(* desphub.pds.backend.services..*(..))")
    public void enableTenantFilter() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null
                && auth.getPrincipal() instanceof AuthenticatedUser user
                && user.officeId() != null) {
            Session session = entityManager.unwrap(Session.class);
            if (session.getEnabledFilter("officeFilter") == null) {
                session.enableFilter("officeFilter").setParameter("officeId", user.officeId());
            }
        }
    }
}
