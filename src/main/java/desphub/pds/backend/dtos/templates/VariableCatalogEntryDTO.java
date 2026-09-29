package desphub.pds.backend.dtos.templates;

import desphub.pds.backend.enums.VariableSource;

public record VariableCatalogEntryDTO(
        VariableSource source,
        String sourceField,
        String suggestedKey,
        String label
) {
}
