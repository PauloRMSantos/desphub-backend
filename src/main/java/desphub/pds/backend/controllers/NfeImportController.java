package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.nfe.NfeImportResponseDTO;
import desphub.pds.backend.services.NfeImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/vehicles/nfe")
public class NfeImportController {

    private final NfeImportService nfeImportService;

    public NfeImportController(NfeImportService nfeImportService) {
        this.nfeImportService = nfeImportService;
    }

    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('NFE_IMPORT')")
    public ResponseEntity<NfeImportResponseDTO> byPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie o arquivo PDF no campo 'file'");
        }
        try {
            return ResponseEntity.ok(nfeImportService.importByPdf(file.getBytes()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível ler o arquivo enviado");
        }
    }
}
