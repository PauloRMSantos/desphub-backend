package desphub.pds.backend.dtos.templates;

import desphub.pds.backend.enums.TemplateCategory;

import java.time.Instant;
import java.util.List;

public record TemplateResponseDTO(
        Long id,
        String name,
        TemplateCategory category,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        List<TemplateBlockDTO> fixedBlocks,
        List<TemplateGroupDTO> groups,
        List<TemplateVariableDTO> variables
) {
}
