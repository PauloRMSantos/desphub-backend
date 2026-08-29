package desphub.pds.backend.dtos.serviceOrderItem;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServiceOrderItemResponseDTO {

    private Long id;
    private Long serviceId;
    private int quantity;
    private BigDecimal unitPrice;
}
