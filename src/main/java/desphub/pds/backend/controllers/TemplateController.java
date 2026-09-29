package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.templates.AiFromDescriptionDTO;
import desphub.pds.backend.dtos.templates.AiFromTextDTO;
import desphub.pds.backend.dtos.templates.CreateTemplateDTO;
import desphub.pds.backend.dtos.templates.TemplateResponseDTO;
import desphub.pds.backend.dtos.templates.TemplateSummaryDTO;
import desphub.pds.backend.dtos.templates.VariableCatalogEntryDTO;
import desphub.pds.backend.enums.TemplateCategory;
import desphub.pds.backend.services.TemplateAiService;
import desphub.pds.backend.services.TemplateService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;
    private final TemplateAiService templateAiService;

    public TemplateController(TemplateService templateService,
                             TemplateAiService templateAiService) {
        this.templateService = templateService;
        this.templateAiService = templateAiService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('TEMPLATES_WRITE')")
    public ResponseEntity<TemplateResponseDTO> create(@Valid @RequestBody CreateTemplateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.create(dto));
    }

    @PostMapping("/ai/from-text")
    @PreAuthorize("hasAuthority('TEMPLATES_WRITE')")
    public ResponseEntity<CreateTemplateDTO> aiFromText(@Valid @RequestBody AiFromTextDTO dto) {
        return ResponseEntity.ok(templateAiService.fromText(dto.rawText(), dto.category()));
    }

    @PostMapping("/ai/from-description")
    @PreAuthorize("hasAuthority('TEMPLATES_WRITE')")
    public ResponseEntity<CreateTemplateDTO> aiFromDescription(@Valid @RequestBody AiFromDescriptionDTO dto) {
        return ResponseEntity.ok(templateAiService.fromDescription(dto.description(), dto.category()));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TEMPLATES_READ')")
    public ResponseEntity<List<TemplateSummaryDTO>> list(
            @RequestParam(required = false) TemplateCategory category) {
        return ResponseEntity.ok(templateService.list(category));
    }

    @GetMapping("/variable-catalog")
    @PreAuthorize("hasAuthority('TEMPLATES_READ')")
    public ResponseEntity<List<VariableCatalogEntryDTO>> variableCatalog() {
        return ResponseEntity.ok(templateService.variableCatalog());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TEMPLATES_READ')")
    public ResponseEntity<TemplateResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TEMPLATES_WRITE')")
    public ResponseEntity<TemplateResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody CreateTemplateDTO dto) {
        return ResponseEntity.ok(templateService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TEMPLATES_WRITE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        templateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
