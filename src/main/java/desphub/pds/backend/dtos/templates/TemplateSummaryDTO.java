package desphub.pds.backend.dtos.templates;

import desphub.pds.backend.enums.TemplateCategory;

import java.time.Instant;

public record TemplateSummaryDTO(
        Long id,
        String name,
        TemplateCategory category,
        boolean active,
        Instant updatedAt
) {
}
