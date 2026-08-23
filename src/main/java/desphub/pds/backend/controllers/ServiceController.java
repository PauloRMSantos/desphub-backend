package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.CreateServiceDTO;
import desphub.pds.backend.dtos.ServiceResponseDTO;
import desphub.pds.backend.services.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDTO> create(@Valid @RequestBody CreateServiceDTO dto) {
        ServiceResponseDTO created = serviceService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
