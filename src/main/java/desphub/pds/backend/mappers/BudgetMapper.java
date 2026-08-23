package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.BudgetItemResponseDTO;
import desphub.pds.backend.dtos.BudgetResponseDTO;
import desphub.pds.backend.models.Budget;
import desphub.pds.backend.models.BudgetItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(target = "clientId", source = "client.id")
    BudgetResponseDTO toResponse(Budget budget);

    @Mapping(target = "serviceId", source = "service.id")
    BudgetItemResponseDTO toItemResponse(BudgetItem item);
}
