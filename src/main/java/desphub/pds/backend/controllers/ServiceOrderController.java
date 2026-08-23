package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.CreateServiceOrderDTO;
import desphub.pds.backend.dtos.ServiceOrderResponseDTO;
import desphub.pds.backend.services.ServiceOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service-orders")
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    public ServiceOrderController(ServiceOrderService serviceOrderService) {
        this.serviceOrderService = serviceOrderService;
    }

    @PostMapping
    public ResponseEntity<ServiceOrderResponseDTO> create(@Valid @RequestBody CreateServiceOrderDTO dto) {
        ServiceOrderResponseDTO created = serviceOrderService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
