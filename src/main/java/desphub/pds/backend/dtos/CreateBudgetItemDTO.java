package desphub.pds.backend.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateBudgetItemDTO {

    @NotNull
    private Long serviceId;

    @Positive
    private int quantity;

    @NotNull
    private BigDecimal unitPrice;
}
