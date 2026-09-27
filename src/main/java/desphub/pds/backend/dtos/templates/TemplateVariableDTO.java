package desphub.pds.backend.dtos.templates;

import desphub.pds.backend.enums.VariableSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TemplateVariableDTO(
        @NotBlank String key,
        @NotBlank String label,
        @NotNull VariableSource source,
        String sourceField,
        Boolean required
) {
}
