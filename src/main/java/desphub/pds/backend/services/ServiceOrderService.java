package desphub.pds.backend.services;

import desphub.pds.backend.dtos.CreateServiceOrderDTO;
import desphub.pds.backend.dtos.CreateServiceOrderItemDTO;
import desphub.pds.backend.dtos.ServiceOrderResponseDTO;
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
import org.springframework.web.server.ResponseStatusException;

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

    public ServiceOrderResponseDTO create(CreateServiceOrderDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado: " + dto.getClientId()));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Veículo não encontrado: " + dto.getVehicleId()));

        ServiceOrder order = new ServiceOrder();
        order.setCode(dto.getCode());
        order.setOrderStatus(dto.getOrderStatus());
        order.setClient(client);
        order.setVehicle(vehicle);
        order.setServicesTotal(dto.getServicesTotal());
        order.setFeesTotal(dto.getFeesTotal());
        order.setTotal(dto.getTotal());

        if (dto.getOriginBudgetId() != null) {
            Budget originBudget = budgetRepository.findById(dto.getOriginBudgetId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Orçamento não encontrado: " + dto.getOriginBudgetId()));
            order.setOriginBudget(originBudget);
        }

        for (CreateServiceOrderItemDTO itemDto : dto.getItems()) {
            Service service = serviceRepository.findById(itemDto.getServiceId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Serviço não encontrado: " + itemDto.getServiceId()));

            ServiceOrderItem item = new ServiceOrderItem();
            item.setService(service);
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(itemDto.getUnitPrice());
            order.addItem(item);
        }

        ServiceOrder saved = serviceOrderRepository.save(order);
        return serviceOrderMapper.toResponse(saved);
    }
}
