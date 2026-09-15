package desphub.pds.backend.dtos.detran;

import java.time.Instant;
import java.util.List;

/** Contrato completo devolvido pelo RPA (mesmo em erro, com status de erro e errors[]). */
public record VehicleQueryResponse(
        String jobId,
        String plate,
        String source,
        Instant collectedAt,
        VehicleData vehicle,
        Licensing licensing,
        Violations violations,
        List<Restriction> restrictions,
        List<Debt> debts,
        String status,
        List<StepError> errors
) {
}
