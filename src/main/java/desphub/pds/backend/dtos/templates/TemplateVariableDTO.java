package desphub.pds.backend.dtos.templates;

import desphub.pds.backend.enums.PartyRole;
import desphub.pds.backend.enums.VariableSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Declaração de uma variável do template e de onde vem o valor. */
public record TemplateVariableDTO(
        @NotBlank String key,
        @NotBlank String label,
        @NotNull VariableSource source,
        String sourceField,   // obrigatório se source != MANUAL (validado no service)
        Boolean required,     // opcional (default false)
        PartyRole partyRole   // OUTORGANTE/OUTORGADO em procurações; null para variáveis comuns
) {
}
