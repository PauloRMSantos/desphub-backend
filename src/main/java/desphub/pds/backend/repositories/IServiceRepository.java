package desphub.pds.backend.repositories;

import desphub.pds.backend.models.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByIdAndOfficeId(Long id, Long officeId);
}
