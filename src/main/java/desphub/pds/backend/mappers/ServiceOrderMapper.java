package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.ServiceOrderItemResponseDTO;
import desphub.pds.backend.dtos.ServiceOrderResponseDTO;
import desphub.pds.backend.models.ServiceOrder;
import desphub.pds.backend.models.ServiceOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceOrderMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "originBudgetId", source = "originBudget.id")
    ServiceOrderResponseDTO toResponse(ServiceOrder order);

    @Mapping(target = "serviceId", source = "service.id")
    ServiceOrderItemResponseDTO toItemResponse(ServiceOrderItem item);
}
