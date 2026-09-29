package desphub.pds.backend.repositories;

import desphub.pds.backend.models.DocumentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IDocumentTemplateRepository extends JpaRepository<DocumentTemplate, Long> {

    Optional<DocumentTemplate> findByIdAndOfficeId(Long id, Long officeId);

    List<DocumentTemplate> findAllByOrderByNameAsc();
}
