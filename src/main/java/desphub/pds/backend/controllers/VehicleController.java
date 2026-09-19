package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.vehicles.CreateVehicleDTO;
import desphub.pds.backend.dtos.vehicles.UpdateVehicleDTO;
import desphub.pds.backend.dtos.vehicles.VehicleGetResponseDTO;
import desphub.pds.backend.dtos.vehicles.VehicleResponseDTO;
import desphub.pds.backend.services.VehicleService;
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
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('VEHICLES_WRITE')")
    public ResponseEntity<VehicleResponseDTO> create(@Valid @RequestBody CreateVehicleDTO dto) {
        VehicleResponseDTO created = vehicleService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VEHICLES_READ')")
    public ResponseEntity<List<VehicleGetResponseDTO>> findAll() {
        return ResponseEntity.ok(vehicleService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VEHICLES_READ')")
    public ResponseEntity<VehicleGetResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('VEHICLES_WRITE')")
    public ResponseEntity<VehicleResponseDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateVehicleDTO dto) {
        return ResponseEntity.ok(vehicleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('VEHICLES_WRITE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
