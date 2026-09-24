package desphub.pds.backend.services;

import desphub.pds.backend.models.User;
import desphub.pds.backend.repositories.IUserRepository;
import desphub.pds.backend.security.CurrentUser;
import desphub.pds.backend.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PasswordService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CurrentUser currentUser;

    public PasswordService(IUserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           CurrentUser currentUser) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.currentUser = currentUser;
    }

    @Transactional
    public void changeOwnPassword(String currentPassword, String newPassword) {
        User user = userRepository.findById(currentUser.get().userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        Claims claims;
        try {
            claims = jwtService.parse(token);
        } catch (Exception e) {
            throw invalidToken();
        }
        if (!JwtService.TYPE_PASSWORD_RESET.equals(claims.get("type", String.class))) {
            throw invalidToken();
        }
        User user = userRepository.findById(Long.valueOf(claims.getSubject())).orElseThrow(this::invalidToken);

        Number pwv = claims.get("pwv", Number.class);
        if (pwv == null || pwv.intValue() != user.getPasswordHash().hashCode()) {
            throw invalidToken();
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private ResponseStatusException invalidToken() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link de redefinição inválido ou expirado");
    }
}
