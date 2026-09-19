package desphub.pds.backend.services;

import desphub.pds.backend.dtos.serviceOrder.CreateServiceOrderDTO;
import desphub.pds.backend.dtos.serviceOrder.ServiceOrderGetResponseDTO;
import desphub.pds.backend.dtos.serviceOrder.ServiceOrderResponseDTO;
import desphub.pds.backend.dtos.serviceOrder.UpdateServiceOrderDTO;
import desphub.pds.backend.dtos.serviceOrderItem.CreateServiceOrderItemDTO;
import desphub.pds.backend.enums.OrderStatusEnum;
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
import desphub.pds.backend.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceOrderService {

    private final IServiceOrderRepository serviceOrderRepository;
    private final IClientRepository clientRepository;
    private final IVehicleRepository vehicleRepository;
    private final IBudgetRepository budgetRepository;
    private final IServiceRepository serviceRepository;
    private final ServiceOrderMapper serviceOrderMapper;
    private final CurrentUser currentUser;

    public ServiceOrderService(IServiceOrderRepository serviceOrderRepository,
                               IClientRepository clientRepository,
                               IVehicleRepository vehicleRepository,
                               IBudgetRepository budgetRepository,
                               IServiceRepository serviceRepository,
                               ServiceOrderMapper serviceOrderMapper,
                               CurrentUser currentUser) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.clientRepository = clientRepository;
        this.vehicleRepository = vehicleRepository;
        this.budgetRepository = budgetRepository;
        this.serviceRepository = serviceRepository;
        this.serviceOrderMapper = serviceOrderMapper;
        this.currentUser = currentUser;
    }

    @Transactional
    public ServiceOrderResponseDTO create(CreateServiceOrderDTO dto) {
        ServiceOrder order = new ServiceOrder();
        order.setOfficeId(currentUser.requireOfficeId());
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
        serviceOrderRepository.delete(findEntityOr404(id));
    }

    private void applyFields(ServiceOrder order, String code, OrderStatusEnum status,
                             Long clientId, Long vehicleId, Long originBudgetId,
                             BigDecimal servicesTotal, BigDecimal feesTotal, BigDecimal total) {
        order.setCode(code);
        order.setOrderStatus(status);
        order.setClient(resolveClient(clientId));
        order.setVehicle(resolveVehicle(vehicleId));
        order.setOriginBudget(resolveBudget(originBudgetId));
        order.setServicesTotal(servicesTotal != null ? servicesTotal : BigDecimal.ZERO);
        order.setFeesTotal(feesTotal != null ? feesTotal : BigDecimal.ZERO);
        order.setTotal(total != null ? total : BigDecimal.ZERO);
    }

    private void applyItems(ServiceOrder order, List<CreateServiceOrderItemDTO> itemDtos) {
        order.getItems().clear();
        for (CreateServiceOrderItemDTO itemDto : itemDtos) {
            Service service = serviceRepository.findByIdAndOfficeId(itemDto.getServiceId(), currentUser.requireOfficeId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Serviço não encontrado: " + itemDto.getServiceId()));

            ServiceOrderItem item = new ServiceOrderItem();
            item.setService(service);
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(itemDto.getUnitPrice());
            order.addItem(item);
        }
    }

    private Client resolveClient(Long clientId) {
        return clientRepository.findByIdAndOfficeId(clientId, currentUser.requireOfficeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado: " + clientId));
    }

    private Vehicle resolveVehicle(Long vehicleId) {
        return vehicleRepository.findByIdAndOfficeId(vehicleId, currentUser.requireOfficeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Veículo não encontrado: " + vehicleId));
    }

    private Budget resolveBudget(Long originBudgetId) {
        if (originBudgetId == null) {
            return null;
        }
        return budgetRepository.findByIdAndOfficeId(originBudgetId, currentUser.requireOfficeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Orçamento não encontrado: " + originBudgetId));
    }

    private ServiceOrder findEntityOr404(Long id) {
        if (currentUser.isAdmin()) {
            return serviceOrderRepository.findById(id).orElseThrow(() -> notFound(id));
        }
        return serviceOrderRepository.findByIdAndOfficeId(id, currentUser.officeId()).orElseThrow(() -> notFound(id));
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de serviço não encontrada: " + id);
    }
}
