package desphub.pds.backend.services;

import desphub.pds.backend.dtos.users.CreateUserDTO;
import desphub.pds.backend.dtos.users.UserResponseDTO;
import desphub.pds.backend.enums.Permission;
import desphub.pds.backend.enums.UserRole;
import desphub.pds.backend.mappers.UserMapper;
import desphub.pds.backend.models.Office;
import desphub.pds.backend.models.User;
import desphub.pds.backend.integrations.EmailService;
import desphub.pds.backend.repositories.IOfficeRepository;
import desphub.pds.backend.repositories.IUserRepository;
import desphub.pds.backend.security.CurrentUser;
import desphub.pds.backend.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final IUserRepository userRepository;
    private final IOfficeRepository officeRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final String resetUrlBase;

    public UserService(IUserRepository userRepository,
                       IOfficeRepository officeRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder,
                       CurrentUser currentUser,
                       JwtService jwtService,
                       EmailService emailService,
                       @Value("${desphub.app.reset-url-base}") String resetUrlBase) {
        this.userRepository = userRepository;
        this.officeRepository = officeRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.currentUser = currentUser;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.resetUrlBase = resetUrlBase;
    }

    @Transactional
    public UserResponseDTO create(Long officeId, CreateUserDTO dto) {
        assertCanCreate(officeId, dto.getRole());

        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Escritório não encontrado: " + officeId));

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com esse e-mail");
        }

        User user = new User();
        user.setOffice(office);
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPermissions(new HashSet<>(dto.getPermissions()));

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findByOffice(Long officeId) {
        assertSameOfficeOrAdmin(officeId);
        return userRepository.findByOfficeId(officeId).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long officeId, Long userId) {
        User user = findManageableOr404(officeId, userId);
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponseDTO updatePermissions(Long officeId, Long userId, Set<Permission> permissions) {
        User user = findManageableOr404(officeId, userId);
        user.setPermissions(new HashSet<>(permissions));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO setActive(Long officeId, Long userId, boolean active) {
        User user = findManageableOr404(officeId, userId);
        user.setActive(active);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long officeId, Long userId) {
        User user = findManageableOr404(officeId, userId);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public void triggerPasswordReset(Long officeId, Long userId) {
        User user = findManageableOr404(officeId, userId);
        String token = jwtService.generatePasswordResetToken(user);
        String separator = resetUrlBase.contains("?") ? "&" : "?";
        String link = resetUrlBase + separator + "token=" + token;
        emailService.sendPasswordReset(user.getEmail(), user.getName(), link);
    }

    // Admin cria qualquer papel em qualquer escritório; owner só cria EMPLOYEE no próprio.
    private void assertCanCreate(Long officeId, UserRole targetRole) {
        if (currentUser.isAdmin()) {
            return;
        }
        assertSameOffice(officeId);
        if (targetRole != UserRole.EMPLOYEE) {
            throw forbidden("Você só pode criar funcionários (EMPLOYEE)");
        }
    }

    private User findManageableOr404(Long officeId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado: " + userId));

        if (user.getOffice() == null || !user.getOffice().getId().equals(officeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado: " + userId);
        }

        if (currentUser.isAdmin()) {
            return user;
        }
        assertSameOffice(officeId);
        // owner só mexe em funcionários, nunca em outro dono/admin
        if (user.getRole() != UserRole.EMPLOYEE) {
            throw forbidden("Você só pode gerenciar funcionários");
        }
        return user;
    }

    private void assertSameOfficeOrAdmin(Long officeId) {
        if (!currentUser.isAdmin()) {
            assertSameOffice(officeId);
        }
    }

    private void assertSameOffice(Long officeId) {
        if (!officeId.equals(currentUser.officeId())) {
            throw forbidden("Você só pode gerenciar usuários do seu escritório");
        }
    }

    private ResponseStatusException forbidden(String message) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }
}
