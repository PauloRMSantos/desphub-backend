package desphub.pds.backend.dtos;

import desphub.pds.backend.enums.OrderStatusEnum;
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
public class CreateServiceOrderDTO {

    @NotBlank
    private String code;

    @NotNull
    private OrderStatusEnum orderStatus;

    @NotNull
    private Long clientId;

    @NotNull
    private Long vehicleId;

    private Long originBudgetId;

    private BigDecimal servicesTotal;
    private BigDecimal feesTotal;
    private BigDecimal total;

    @Valid
    private List<CreateServiceOrderItemDTO> items = new ArrayList<>();
}
