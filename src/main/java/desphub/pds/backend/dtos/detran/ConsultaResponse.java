package desphub.pds.backend.dtos.detran;

import java.time.Instant;
import java.util.List;

public record ConsultaResponse(
        String jobId,
        String placa,
        String fonte,
        Instant coletadoEm,
        Veiculo veiculo,
        Licenciamento licenciamento,
        Infracoes infracoes,
        List<Restricao> restricoes,
        List<Debito> debitos,
        String status,
        List<EtapaErro> erros
) {
}
