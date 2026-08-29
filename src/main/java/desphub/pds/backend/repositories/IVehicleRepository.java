package desphub.pds.backend.repositories;

import desphub.pds.backend.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IVehicleRepository extends JpaRepository<Vehicle, Long> {
}
