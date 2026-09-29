package desphub.pds.backend.repositories;

import desphub.pds.backend.models.GeneratedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IGeneratedDocumentRepository extends JpaRepository<GeneratedDocument, Long> {

    Optional<GeneratedDocument> findByIdAndOfficeId(Long id, Long officeId);

    List<GeneratedDocument> findAllByOrderByCreatedAtDesc();

    List<GeneratedDocument> findByClientIdOrderByCreatedAtDesc(Long clientId);
}
