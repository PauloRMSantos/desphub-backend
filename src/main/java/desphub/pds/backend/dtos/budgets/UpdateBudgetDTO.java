package desphub.pds.backend.dtos.budgets;

import desphub.pds.backend.dtos.budgetItens.CreateBudgetItemDTO;
import desphub.pds.backend.enums.StatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UpdateBudgetDTO {

    @NotBlank
    private String code;

    @NotNull
    private StatusEnum status;

    private Long clientId;

    private BigDecimal totalPrice;

    // a lista enviada substitui a atual por completo
    @Valid
    private List<CreateBudgetItemDTO> items = new ArrayList<>();
}
