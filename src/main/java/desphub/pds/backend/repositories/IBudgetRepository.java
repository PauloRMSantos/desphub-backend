package desphub.pds.backend.repositories;

import desphub.pds.backend.models.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IBudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByIdAndOfficeId(Long id, Long officeId);
}
