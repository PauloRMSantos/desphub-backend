package desphub.pds.backend.services;

import desphub.pds.backend.dtos.budgetItens.CreateBudgetItemDTO;
import desphub.pds.backend.dtos.budgets.BudgetGetResponseDTO;
import desphub.pds.backend.dtos.budgets.BudgetResponseDTO;
import desphub.pds.backend.dtos.budgets.CreateBudgetDTO;
import desphub.pds.backend.dtos.budgets.UpdateBudgetDTO;
import desphub.pds.backend.mappers.BudgetMapper;
import desphub.pds.backend.models.Budget;
import desphub.pds.backend.models.BudgetItem;
import desphub.pds.backend.models.Client;
import desphub.pds.backend.models.Service;
import desphub.pds.backend.repositories.IBudgetRepository;
import desphub.pds.backend.repositories.IClientRepository;
import desphub.pds.backend.repositories.IServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// "Service" (entidade) colide com a anotação @Service do Spring, por isso qualificada
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

    @Transactional
    public BudgetResponseDTO create(CreateBudgetDTO dto) {
        Budget budget = new Budget();
        budget.setCode(dto.getCode());
        budget.setStatus(dto.getStatus());
        budget.setTotalPrice(dto.getTotalPrice());
        budget.setClient(resolveClient(dto.getClientId()));
        applyItems(budget, dto.getItems());
        return budgetMapper.toResponse(budgetRepository.save(budget));
    }

    @Transactional(readOnly = true)
    public List<BudgetGetResponseDTO> findAll() {
        return budgetRepository.findAll().stream()
                .map(budgetMapper::toGetResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BudgetGetResponseDTO findById(Long id) {
        return budgetMapper.toGetResponse(findEntityOr404(id));
    }

    @Transactional
    public BudgetResponseDTO update(Long id, UpdateBudgetDTO dto) {
        Budget budget = findEntityOr404(id);
        budget.setCode(dto.getCode());
        budget.setStatus(dto.getStatus());
        budget.setTotalPrice(dto.getTotalPrice());
        budget.setClient(resolveClient(dto.getClientId()));
        applyItems(budget, dto.getItems());
        return budgetMapper.toResponse(budgetRepository.save(budget));
    }

    @Transactional
    public void delete(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw notFound(id);
        }
        budgetRepository.deleteById(id);
    }

    // limpa os itens atuais e reconstrói a partir do DTO (orphanRemoval apaga os antigos)
    private void applyItems(Budget budget, List<CreateBudgetItemDTO> itemDtos) {
        budget.getItems().clear();
        for (CreateBudgetItemDTO itemDto : itemDtos) {
            Service service = serviceRepository.findById(itemDto.getServiceId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Serviço não encontrado: " + itemDto.getServiceId()));

            BudgetItem item = new BudgetItem();
            item.setBudget(budget);            // lado dono do relacionamento (FK)
            item.setService(service);
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(itemDto.getUnitPrice());
            budget.addItem(item);
        }
    }

    // clientId é opcional: null significa orçamento sem cliente vinculado
    private Client resolveClient(Long clientId) {
        if (clientId == null) {
            return null;
        }
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado: " + clientId));
    }

    private Budget findEntityOr404(Long id) {
        return budgetRepository.findById(id).orElseThrow(() -> notFound(id));
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento não encontrado: " + id);
    }
}
