package desphub.pds.backend.dtos.serviceOrderItem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateServiceOrderItemDTO {

    @NotNull
    private Long serviceId;

    @Positive
    private int quantity;

    @NotNull
    private BigDecimal unitPrice;
}
