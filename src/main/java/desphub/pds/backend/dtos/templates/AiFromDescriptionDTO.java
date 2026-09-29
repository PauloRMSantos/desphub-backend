package desphub.pds.backend.dtos.templates;

import desphub.pds.backend.enums.TemplateCategory;
import jakarta.validation.constraints.NotBlank;

public record AiFromDescriptionDTO(
        @NotBlank String description,
        TemplateCategory category
) {
}
