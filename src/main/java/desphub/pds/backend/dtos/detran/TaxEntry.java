package desphub.pds.backend.dtos.detran;

import java.math.BigDecimal;

public record TaxEntry(
        String year,
        String status,
        BigDecimal amount,
        String dueDate,
        boolean activeDebt
) {
}
