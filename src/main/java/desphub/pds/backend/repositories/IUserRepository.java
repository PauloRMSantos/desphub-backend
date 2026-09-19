package desphub.pds.backend.repositories;

import desphub.pds.backend.enums.UserRole;
import desphub.pds.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(UserRole role);

    List<User> findByOfficeId(Long officeId);
}
