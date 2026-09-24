package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.expenses.CreateExpenseDTO;
import desphub.pds.backend.dtos.expenses.ExpenseResponseDTO;
import desphub.pds.backend.dtos.expenses.UpdateExpenseDTO;
import desphub.pds.backend.models.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "officeId", ignore = true)
    Expense toEntity(CreateExpenseDTO dto);

    ExpenseResponseDTO toResponse(Expense expense);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "officeId", ignore = true)
    void updateEntity(UpdateExpenseDTO dto, @MappingTarget Expense entity);
}
