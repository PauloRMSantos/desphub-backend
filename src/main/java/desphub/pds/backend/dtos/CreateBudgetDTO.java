package desphub.pds.backend.dtos;

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
public class CreateBudgetDTO {

    @NotBlank
    private String code;

    @NotNull
    private StatusEnum status;

    private Long clientId;

    private BigDecimal totalPrice;

    @Valid
    private List<CreateBudgetItemDTO> items = new ArrayList<>();
}
