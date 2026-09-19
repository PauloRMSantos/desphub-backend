package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.detran.VehicleQueryResponse;
import desphub.pds.backend.integrations.RpaDetranClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleQueryController {

    private final RpaDetranClient rpaDetranClient;

    public VehicleQueryController(RpaDetranClient rpaDetranClient) {
        this.rpaDetranClient = rpaDetranClient;
    }

    // GET /api/vehicles/query?plate=ABC1234&renavam=2345
    @GetMapping("/query")
    @PreAuthorize("hasAuthority('VEHICLE_QUERY')")
    public ResponseEntity<VehicleQueryResponse> query(@RequestParam(required = false) String plate,
                                                      @RequestParam(required = false) String renavam) {
        if (isBlank(plate) && isBlank(renavam)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe placa ou renavam");
        }
        return ResponseEntity.ok(rpaDetranClient.query(plate, renavam));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
