package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.auth.AuthUserDTO;
import desphub.pds.backend.dtos.auth.ChangePasswordRequest;
import desphub.pds.backend.dtos.auth.LoginRequest;
import desphub.pds.backend.dtos.auth.LoginResponse;
import desphub.pds.backend.dtos.auth.ResetPasswordRequest;
import desphub.pds.backend.models.User;
import desphub.pds.backend.repositories.IUserRepository;
import desphub.pds.backend.security.AuthenticatedUser;
import desphub.pds.backend.security.JwtService;
import desphub.pds.backend.services.PasswordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordService passwordService;

    public AuthController(IUserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.passwordService = passwordService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(this::invalidCredentials);
        if (!user.isActive() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw invalidCredentials();
        }
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(
                token, "Bearer", jwtService.getExpirationSeconds(), toAuthUser(user)));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUserDTO> me(@AuthenticationPrincipal AuthenticatedUser principal) {
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));
        return ResponseEntity.ok(toAuthUser(user));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        passwordService.changeOwnPassword(request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    private AuthUserDTO toAuthUser(User user) {
        return new AuthUserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getOffice() != null ? user.getOffice().getId() : null,
                user.getPermissions());
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    }
}
