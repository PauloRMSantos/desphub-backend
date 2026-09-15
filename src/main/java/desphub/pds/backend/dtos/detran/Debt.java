package desphub.pds.backend.dtos.detran;

import java.math.BigDecimal;

public record Debt(String type, Integer year, BigDecimal amount, String dueDate) {
}
