package desphub.pds.backend.repositories;

import desphub.pds.backend.models.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOfficeRepository extends JpaRepository<Office, Long> {
    boolean existsByCpfCnpj(String cpfCnpj);
}
