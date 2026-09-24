package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.users.CreateUserDTO;
import desphub.pds.backend.dtos.users.UpdateActiveRequest;
import desphub.pds.backend.dtos.users.UpdatePermissionsRequest;
import desphub.pds.backend.dtos.users.UserResponseDTO;
import desphub.pds.backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offices/{officeId}/users")
@PreAuthorize("hasAuthority('USERS_MANAGE')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@PathVariable Long officeId,
                                                  @Valid @RequestBody CreateUserDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(officeId, dto));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll(@PathVariable Long officeId) {
        return ResponseEntity.ok(userService.findByOffice(officeId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long officeId, @PathVariable Long userId) {
        return ResponseEntity.ok(userService.findById(officeId, userId));
    }

    @PutMapping("/{userId}/permissions")
    public ResponseEntity<UserResponseDTO> updatePermissions(@PathVariable Long officeId,
                                                             @PathVariable Long userId,
                                                             @Valid @RequestBody UpdatePermissionsRequest request) {
        return ResponseEntity.ok(userService.updatePermissions(officeId, userId, request.permissions()));
    }

    @PatchMapping("/{userId}/active")
    public ResponseEntity<UserResponseDTO> setActive(@PathVariable Long officeId,
                                                     @PathVariable Long userId,
                                                     @Valid @RequestBody UpdateActiveRequest request) {
        return ResponseEntity.ok(userService.setActive(officeId, userId, request.active()));
    }

    @PostMapping("/{userId}/password-reset")
    public ResponseEntity<Void> sendPasswordReset(@PathVariable Long officeId, @PathVariable Long userId) {
        userService.triggerPasswordReset(officeId, userId);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long officeId, @PathVariable Long userId) {
        userService.delete(officeId, userId);
        return ResponseEntity.noContent().build();
    }
}
