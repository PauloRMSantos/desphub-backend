package desphub.pds.backend.dtos.detran;

import java.math.BigDecimal;

public record Debito(String tipo, Integer exercicio, BigDecimal valor, String vencimento) {
}
