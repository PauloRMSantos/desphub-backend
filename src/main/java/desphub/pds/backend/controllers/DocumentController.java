package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.documents.GenerateDocumentDTO;
import desphub.pds.backend.dtos.documents.GeneratedDocumentResponseDTO;
import desphub.pds.backend.dtos.documents.GeneratedDocumentSummaryDTO;
import desphub.pds.backend.services.DocumentGenerationService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentGenerationService documentService;

    public DocumentController(DocumentGenerationService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('DOCUMENTS_WRITE')")
    public ResponseEntity<GeneratedDocumentResponseDTO> generate(@Valid @RequestBody GenerateDocumentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.generate(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DOCUMENTS_READ')")
    public ResponseEntity<List<GeneratedDocumentSummaryDTO>> list(
            @RequestParam(required = false) Long clientId) {
        return ResponseEntity.ok(documentService.list(clientId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCUMENTS_READ')")
    public ResponseEntity<GeneratedDocumentResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getById(id));
    }

    // reimpressão: regenera o PDF a partir do texto resolvido guardado
    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAuthority('DOCUMENTS_READ')")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        byte[] pdf = documentService.pdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename("documento-" + id + ".pdf").toString())
                .body(pdf);
    }
}
