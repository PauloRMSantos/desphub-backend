package desphub.pds.backend.dtos.detran;

import java.util.List;

public record VehicleQueryRequest(String plate, String renavam, List<String> types) {
}
