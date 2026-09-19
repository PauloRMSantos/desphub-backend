package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.offices.CreateOfficeDTO;
import desphub.pds.backend.dtos.offices.OfficeResponseDTO;
import desphub.pds.backend.services.OfficeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/offices")
@PreAuthorize("hasRole('DESPHUB_ADMIN')")
public class OfficeController {

    private final OfficeService officeService;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @PostMapping
    public ResponseEntity<OfficeResponseDTO> create(@Valid @RequestBody CreateOfficeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(officeService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<OfficeResponseDTO>> findAll() {
        return ResponseEntity.ok(officeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfficeResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(officeService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        officeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
