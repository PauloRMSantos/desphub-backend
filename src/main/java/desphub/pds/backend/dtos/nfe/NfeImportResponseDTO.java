package desphub.pds.backend.dtos.nfe;

import desphub.pds.backend.dtos.vehicles.CreateVehicleDTO;

import java.util.List;

public record NfeImportResponseDTO(
        NfeAccessKeyInfo accessKey,
        CreateVehicleDTO vehicle,
        List<String> warnings
) {
}
