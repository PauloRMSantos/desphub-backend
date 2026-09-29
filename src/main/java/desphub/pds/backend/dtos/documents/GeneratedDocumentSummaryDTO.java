package desphub.pds.backend.dtos.documents;

import java.time.Instant;

public record GeneratedDocumentSummaryDTO(
        Long id,
        String templateName,
        String clientName,
        Instant createdAt
) {
}
