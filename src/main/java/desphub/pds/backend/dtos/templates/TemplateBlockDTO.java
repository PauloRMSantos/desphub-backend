package desphub.pds.backend.dtos.templates;

import jakarta.validation.constraints.NotBlank;

public record TemplateBlockDTO(
        Long id,
        @NotBlank String label,
        @NotBlank String body,
        Integer sortOrder,
        Boolean defaultSelected
) {
}
