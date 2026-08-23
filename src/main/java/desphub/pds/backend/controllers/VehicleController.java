package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.CreateVehicleDTO;
import desphub.pds.backend.dtos.VehicleResponseDTO;
import desphub.pds.backend.services.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<VehicleResponseDTO> create(@Valid @RequestBody CreateVehicleDTO dto) {
        VehicleResponseDTO created = vehicleService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
