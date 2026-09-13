package desphub.pds.backend.dtos.detran;

import java.util.List;

public record ConsultaRequest(String placa, String renavam, List<String> tipos) {
}
