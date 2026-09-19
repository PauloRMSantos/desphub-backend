package desphub.pds.backend.repositories;

import desphub.pds.backend.models.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    Optional<ServiceOrder> findByIdAndOfficeId(Long id, Long officeId);
}
