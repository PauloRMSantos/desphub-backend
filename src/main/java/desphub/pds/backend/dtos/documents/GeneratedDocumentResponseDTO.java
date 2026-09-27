package desphub.pds.backend.dtos.documents;

import java.time.Instant;

public record GeneratedDocumentResponseDTO(
        Long id,
        Long templateId,
        String templateName,
        Long clientId,
        String clientName,
        Long vehicleId,
        Instant createdAt,
        String resolvedContent
) {
}
