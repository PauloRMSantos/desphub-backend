package desphub.pds.backend.repositories;

import desphub.pds.backend.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IClientRepository extends JpaRepository<Client, Long> {
    // operações por PK: filtram por escritório explicitamente (o @Filter não cobre find-by-id)
    Optional<Client> findByIdAndOfficeId(Long id, Long officeId);
}
