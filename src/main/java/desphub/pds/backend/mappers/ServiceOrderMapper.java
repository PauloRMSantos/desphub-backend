package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.serviceOrder.ServiceOrderGetResponseDTO;
import desphub.pds.backend.dtos.serviceOrder.ServiceOrderResponseDTO;
import desphub.pds.backend.dtos.serviceOrderItem.ServiceOrderItemResponseDTO;
import desphub.pds.backend.models.ServiceOrder;
import desphub.pds.backend.models.ServiceOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Assim como o BudgetMapper, a montagem da entidade (client, vehicle, budget de
 * origem e itens) fica no service. Aqui só mapeamos as respostas.
 */
@Mapper(componentModel = "spring")
public interface ServiceOrderMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "originBudgetId", source = "originBudget.id")
    ServiceOrderResponseDTO toResponse(ServiceOrder order);

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "originBudgetId", source = "originBudget.id")
    ServiceOrderGetResponseDTO toGetResponse(ServiceOrder order);

    @Mapping(target = "serviceId", source = "service.id")
    ServiceOrderItemResponseDTO toItemResponse(ServiceOrderItem item);
}
