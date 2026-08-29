package desphub.pds.backend.dtos.budgetItens;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BudgetItemResponseDTO {

    private Long id;
    private Long serviceId;
    private int quantity;
    private BigDecimal unitPrice;
}
