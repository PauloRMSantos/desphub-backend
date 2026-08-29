package desphub.pds.backend.dtos.serviceOrder;

import desphub.pds.backend.dtos.serviceOrderItem.ServiceOrderItemResponseDTO;
import desphub.pds.backend.enums.OrderStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ServiceOrderGetResponseDTO {

    private Long id;
    private String code;
    private OrderStatusEnum orderStatus;
    private Long clientId;
    private Long vehicleId;
    private Long originBudgetId;
    private BigDecimal servicesTotal;
    private BigDecimal feesTotal;
    private BigDecimal total;
    private List<ServiceOrderItemResponseDTO> items;
}
