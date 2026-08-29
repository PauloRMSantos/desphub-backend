package desphub.pds.backend.services;

import desphub.pds.backend.dtos.serviceOrder.CreateServiceOrderDTO;
import desphub.pds.backend.dtos.serviceOrder.ServiceOrderGetResponseDTO;
import desphub.pds.backend.dtos.serviceOrder.ServiceOrderResponseDTO;
import desphub.pds.backend.dtos.serviceOrder.UpdateServiceOrderDTO;
import desphub.pds.backend.dtos.serviceOrderItem.CreateServiceOrderItemDTO;
import desphub.pds.backend.mappers.ServiceOrderMapper;
import desphub.pds.backend.models.Budget;
import desphub.pds.backend.models.Client;
import desphub.pds.backend.models.Service;
import desphub.pds.backend.models.ServiceOrder;
import desphub.pds.backend.models.ServiceOrderItem;
import desphub.pds.backend.models.Vehicle;
import desphub.pds.backend.repositories.IBudgetRepository;
import desphub.pds.backend.repositories.IClientRepository;
import desphub.pds.backend.repositories.IServiceOrderRepository;
import desphub.pds.backend.repositories.IServiceRepository;
import desphub.pds.backend.repositories.IVehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// "Service" (entidade) colide com a anotação @Service do Spring, por isso qualificada
@org.springframework.stereotype.Service
public class ServiceOrderService {

    private final IServiceOrderRepository serviceOrderRepository;
    private final IClientRepository clientRepository;
    private final IVehicleRepository vehicleRepository;
    private final IBudgetRepository budgetRepository;
    private final IServiceRepository serviceRepository;
    private final ServiceOrderMapper serviceOrderMapper;

    public ServiceOrderService(IServiceOrderRepository serviceOrderRepository,
                               IClientRepository clientRepository,
                               IVehicleRepository vehicleRepository,
                               IBudgetRepository budgetRepository,
                               IServiceRepository serviceRepository,
                               ServiceOrderMapper serviceOrderMapper) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.clientRepository = clientRepository;
        this.vehicleRepository = vehicleRepository;
        this.budgetRepository = budgetRepository;
        this.serviceRepository = serviceRepository;
        this.serviceOrderMapper = serviceOrderMapper;
    }

    @Transactional
    public ServiceOrderResponseDTO create(CreateServiceOrderDTO dto) {
        ServiceOrder order = new ServiceOrder();
        applyFields(order, dto.getCode(), dto.getOrderStatus(), dto.getClientId(), dto.getVehicleId(),
                dto.getOriginBudgetId(), dto.getServicesTotal(), dto.getFeesTotal(), dto.getTotal());
        applyItems(order, dto.getItems());
        return serviceOrderMapper.toResponse(serviceOrderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<ServiceOrderGetResponseDTO> findAll() {
        return serviceOrderRepository.findAll().stream()
                .map(serviceOrderMapper::toGetResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceOrderGetResponseDTO findById(Long id) {
        return serviceOrderMapper.toGetResponse(findEntityOr404(id));
    }

    @Transactional
    public ServiceOrderResponseDTO update(Long id, UpdateServiceOrderDTO dto) {
        ServiceOrder order = findEntityOr404(id);
        applyFields(order, dto.getCode(), dto.getOrderStatus(), dto.getClientId(), dto.getVehicleId(),
                dto.getOriginBudgetId(), dto.getServicesTotal(), dto.getFeesTotal(), dto.getTotal());
        applyItems(order, dto.getItems());
        return serviceOrderMapper.toResponse(serviceOrderRepository.save(order));
    }

    @Transactional
    public void delete(Long id) {
        if (!serviceOrderRepository.existsById(id)) {
            throw notFound(id);
        }
        serviceOrderRepository.deleteById(id);
    }

    private void applyFields(ServiceOrder order, String code, desphub.pds.backend.enums.OrderStatusEnum status,
                             Long clientId, Long vehicleId, Long originBudgetId,
                             java.math.BigDecimal servicesTotal, java.math.BigDecimal feesTotal,
                             java.math.BigDecimal total) {
        order.setCode(code);
        order.setOrderStatus(status);
        order.setClient(resolveClient(clientId));
        order.setVehicle(resolveVehicle(vehicleId));
        order.setOriginBudget(resolveBudget(originBudgetId));
        order.setServicesTotal(servicesTotal);
        order.setFeesTotal(feesTotal);
        order.setTotal(total);
    }

    // limpa os itens atuais e reconstrói a partir do DTO (orphanRemoval apaga os antigos)
    private void applyItems(ServiceOrder order, List<CreateServiceOrderItemDTO> itemDtos) {
        order.getItems().clear();
        for (CreateServiceOrderItemDTO itemDto : itemDtos) {
            Service service = serviceRepository.findById(itemDto.getServiceId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Serviço não encontrado: " + itemDto.getServiceId()));

            ServiceOrderItem item = new ServiceOrderItem();
            item.setService(service);
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(itemDto.getUnitPrice());
            order.addItem(item); // já seta o back-reference (item.setOrder(this))
        }
    }

    private Client resolveClient(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado: " + clientId));
    }

    private Vehicle resolveVehicle(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Veículo não encontrado: " + vehicleId));
    }

    // originBudgetId é opcional: null significa OS sem orçamento de origem
    private Budget resolveBudget(Long originBudgetId) {
        if (originBudgetId == null) {
            return null;
        }
        return budgetRepository.findById(originBudgetId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Orçamento não encontrado: " + originBudgetId));
    }

    private ServiceOrder findEntityOr404(Long id) {
        return serviceOrderRepository.findById(id).orElseThrow(() -> notFound(id));
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de serviço não encontrada: " + id);
    }
}
