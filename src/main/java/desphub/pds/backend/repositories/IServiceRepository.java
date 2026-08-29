package desphub.pds.backend.repositories;

import desphub.pds.backend.models.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IServiceRepository extends JpaRepository<Service, Long> {
}
