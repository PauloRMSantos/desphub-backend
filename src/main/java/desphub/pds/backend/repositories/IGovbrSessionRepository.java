package desphub.pds.backend.repositories;

import desphub.pds.backend.models.GovbrSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGovbrSessionRepository extends JpaRepository<GovbrSession, Long> {
}
