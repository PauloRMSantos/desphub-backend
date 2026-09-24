package desphub.pds.backend.security;

import desphub.pds.backend.enums.Permission;
import desphub.pds.backend.enums.UserRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.parse(header.substring(7));

                if (claims.get("type", String.class) != null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                Long userId = Long.valueOf(claims.getSubject());
                Number officeIdClaim = claims.get("officeId", Number.class);
                Long officeId = officeIdClaim != null ? officeIdClaim.longValue() : null;
                UserRole role = UserRole.valueOf(claims.get("role", String.class));

                @SuppressWarnings("unchecked")
                List<String> rawPermissions = claims.get("permissions", List.class);
                Set<Permission> permissions = rawPermissions == null ? Set.of()
                        : rawPermissions.stream().map(Permission::valueOf).collect(Collectors.toSet());

                AuthenticatedUser principal = new AuthenticatedUser(userId, officeId, role, permissions);
                var authentication = new UsernamePasswordAuthenticationToken(
                        principal, null, buildAuthorities(role, permissions));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private List<GrantedAuthority> buildAuthorities(UserRole role, Set<Permission> permissions) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        Set<Permission> effective = (role == UserRole.EMPLOYEE) ? permissions : EnumSet.allOf(Permission.class);
        effective.forEach(p -> authorities.add(new SimpleGrantedAuthority(p.name())));
        return authorities;
    }
}
