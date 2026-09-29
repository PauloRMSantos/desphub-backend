package desphub.pds.backend.dtos.templates;

import desphub.pds.backend.enums.SelectionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TemplateGroupDTO(
        @NotBlank String key,
        @NotBlank String label,
        @NotNull SelectionType selectionType,
        Boolean required,
        Integer sortOrder,
        @Valid @NotEmpty List<TemplateBlockDTO> blocks
) {
}
