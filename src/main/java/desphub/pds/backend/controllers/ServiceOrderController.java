package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.serviceOrder.CreateServiceOrderDTO;
import desphub.pds.backend.dtos.serviceOrder.ServiceOrderGetResponseDTO;
import desphub.pds.backend.dtos.serviceOrder.ServiceOrderResponseDTO;
import desphub.pds.backend.dtos.serviceOrder.UpdateServiceOrderDTO;
import desphub.pds.backend.services.ServiceOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/service-orders")
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    public ServiceOrderController(ServiceOrderService serviceOrderService) {
        this.serviceOrderService = serviceOrderService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SERVICE_ORDERS_WRITE')")
    public ResponseEntity<ServiceOrderResponseDTO> create(@Valid @RequestBody CreateServiceOrderDTO dto) {
        ServiceOrderResponseDTO created = serviceOrderService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SERVICE_ORDERS_READ')")
    public ResponseEntity<List<ServiceOrderGetResponseDTO>> findAll() {
        return ResponseEntity.ok(serviceOrderService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SERVICE_ORDERS_READ')")
    public ResponseEntity<ServiceOrderGetResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceOrderService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SERVICE_ORDERS_WRITE')")
    public ResponseEntity<ServiceOrderResponseDTO> update(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateServiceOrderDTO dto) {
        return ResponseEntity.ok(serviceOrderService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SERVICE_ORDERS_WRITE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
