package desphub.pds.backend.dtos.documents;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record GenerateDocumentDTO(
        @NotNull Long templateId,
        @NotNull Long clientId,
        Long vehicleId,
        List<Long> selectedBlockIds,
        Map<String, String> manualValues
) {
}
