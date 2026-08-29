package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.budgetItens.BudgetItemResponseDTO;
import desphub.pds.backend.dtos.budgets.BudgetGetResponseDTO;
import desphub.pds.backend.dtos.budgets.BudgetResponseDTO;
import desphub.pds.backend.models.Budget;
import desphub.pds.backend.models.BudgetItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * A construção da entidade Budget (resolução de FKs e itens) fica no service,
 * porque envolve buscar Client/Service nos repositórios. O mapper cuida só do
 * caminho de saída (entidade -> DTOs de resposta).
 */
@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(target = "clientId", source = "client.id")
    BudgetResponseDTO toResponse(Budget budget);

    @Mapping(target = "clientId", source = "client.id")
    BudgetGetResponseDTO toGetResponse(Budget budget);

    @Mapping(target = "serviceId", source = "service.id")
    BudgetItemResponseDTO toItemResponse(BudgetItem item);
}
