package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.detran.ConsultaResponse;
import desphub.pds.backend.integrations.RpaDetranClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/vehicles")
public class ConsultaVeicularController {

    private final RpaDetranClient rpaDetranClient;

    public ConsultaVeicularController(RpaDetranClient rpaDetranClient) {
        this.rpaDetranClient = rpaDetranClient;
    }

    // GET /api/vehicles/consulta?placa=XXX&renavam=XXX
    @GetMapping("/consulta")
    public ResponseEntity<ConsultaResponse> consultar(@RequestParam(required = false) String placa,
                                                      @RequestParam(required = false) String renavam) {
        if (isBlank(placa) && isBlank(renavam)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe placa ou renavam");
        }
        return ResponseEntity.ok(rpaDetranClient.consultar(placa, renavam));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
