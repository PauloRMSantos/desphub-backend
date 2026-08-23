package desphub.pds.backend.dtos;

import desphub.pds.backend.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class BudgetResponseDTO {

    private Long id;
    private String code;
    private StatusEnum status;
    private Long clientId;
    private BigDecimal totalPrice;
    private List<BudgetItemResponseDTO> items;
}
