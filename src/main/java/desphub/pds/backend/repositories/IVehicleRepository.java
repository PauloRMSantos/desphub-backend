package desphub.pds.backend.repositories;

import desphub.pds.backend.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IVehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByIdAndOfficeId(Long id, Long officeId);
}
