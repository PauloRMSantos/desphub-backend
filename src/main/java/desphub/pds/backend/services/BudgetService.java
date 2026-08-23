package desphub.pds.backend.services;

import desphub.pds.backend.dtos.BudgetResponseDTO;
import desphub.pds.backend.dtos.CreateBudgetDTO;
import desphub.pds.backend.dtos.CreateBudgetItemDTO;
import desphub.pds.backend.mappers.BudgetMapper;
import desphub.pds.backend.models.Budget;
import desphub.pds.backend.models.BudgetItem;
import desphub.pds.backend.models.Client;
import desphub.pds.backend.models.Service;
import desphub.pds.backend.repositories.IBudgetRepository;
import desphub.pds.backend.repositories.IClientRepository;
import desphub.pds.backend.repositories.IServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@org.springframework.stereotype.Service
public class BudgetService {

    private final IBudgetRepository budgetRepository;
    private final IClientRepository clientRepository;
    private final IServiceRepository serviceRepository;
    private final BudgetMapper budgetMapper;

    public BudgetService(IBudgetRepository budgetRepository,
                         IClientRepository clientRepository,
                         IServiceRepository serviceRepository,
                         BudgetMapper budgetMapper) {
        this.budgetRepository = budgetRepository;
        this.clientRepository = clientRepository;
        this.serviceRepository = serviceRepository;
        this.budgetMapper = budgetMapper;
    }

    public BudgetResponseDTO create(CreateBudgetDTO dto) {
        Budget budget = new Budget();
        budget.setCode(dto.getCode());
        budget.setStatus(dto.getStatus());
        budget.setTotalPrice(dto.getTotalPrice());

        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Cliente não encontrado: " + dto.getClientId()));
            budget.setClient(client);
        }

        for (CreateBudgetItemDTO itemDto : dto.getItems()) {
            Service service = serviceRepository.findById(itemDto.getServiceId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Serviço não encontrado: " + itemDto.getServiceId()));

            BudgetItem item = new BudgetItem();
            item.setBudget(budget);
            item.setService(service);
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(itemDto.getUnitPrice());
            budget.addItem(item);
        }

        Budget saved = budgetRepository.save(budget);
        return budgetMapper.toResponse(saved);
    }
}
