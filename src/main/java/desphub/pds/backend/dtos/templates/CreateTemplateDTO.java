package desphub.pds.backend.dtos.templates;

import desphub.pds.backend.enums.TemplateCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateTemplateDTO(
        @NotBlank String name,
        @NotNull TemplateCategory category,
        Boolean active,
        @Valid List<TemplateBlockDTO> fixedBlocks,
        @Valid List<TemplateGroupDTO> groups,
        @Valid List<TemplateVariableDTO> variables
) {
}
