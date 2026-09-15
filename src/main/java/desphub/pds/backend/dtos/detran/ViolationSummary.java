package desphub.pds.backend.dtos.detran;

import java.math.BigDecimal;

public record ViolationSummary(int count, BigDecimal amount) {
}
