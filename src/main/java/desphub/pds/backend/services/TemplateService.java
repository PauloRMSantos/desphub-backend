package desphub.pds.backend.services;

import desphub.pds.backend.dtos.templates.CreateTemplateDTO;
import desphub.pds.backend.dtos.templates.TemplateResponseDTO;
import desphub.pds.backend.dtos.templates.TemplateSummaryDTO;
import desphub.pds.backend.dtos.templates.TemplateVariableDTO;
import desphub.pds.backend.dtos.templates.VariableCatalogEntryDTO;
import desphub.pds.backend.enums.PartyRole;
import desphub.pds.backend.enums.TemplateCategory;
import desphub.pds.backend.enums.VariableSource;
import desphub.pds.backend.mappers.TemplateMapper;
import desphub.pds.backend.models.DocumentTemplate;
import desphub.pds.backend.repositories.IDocumentTemplateRepository;
import desphub.pds.backend.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TemplateService {

    private final IDocumentTemplateRepository templateRepository;
    private final TemplateMapper templateMapper;
    private final VariableCatalog variableCatalog;
    private final CurrentUser currentUser;

    public TemplateService(IDocumentTemplateRepository templateRepository,
                           TemplateMapper templateMapper,
                           VariableCatalog variableCatalog,
                           CurrentUser currentUser) {
        this.templateRepository = templateRepository;
        this.templateMapper = templateMapper;
        this.variableCatalog = variableCatalog;
        this.currentUser = currentUser;
    }

    @Transactional
    public TemplateResponseDTO create(CreateTemplateDTO dto) {
        validateVariables(dto);
        validateProcuracaoParties(dto);
        DocumentTemplate template = new DocumentTemplate();
        template.setOfficeId(currentUser.requireOfficeId());
        template.setCreatedByUserId(currentUser.get().userId());
        templateMapper.apply(dto, template);
        return templateMapper.toResponse(templateRepository.save(template));
    }

    @Transactional(readOnly = true)
    public List<TemplateSummaryDTO> list(TemplateCategory category) {
        return templateRepository.findAllByOrderByNameAsc().stream()
                .filter(t -> category == null || t.getCategory() == category)
                .map(templateMapper::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public TemplateResponseDTO getById(Long id) {
        return templateMapper.toResponse(findEntityOr404(id));
    }

    @Transactional
    public TemplateResponseDTO update(Long id, CreateTemplateDTO dto) {
        validateVariables(dto);
        validateProcuracaoParties(dto);
        DocumentTemplate template = findEntityOr404(id);
        templateMapper.apply(dto, template);
        return templateMapper.toResponse(templateRepository.save(template));
    }

    @Transactional
    public void delete(Long id) {
        templateRepository.delete(findEntityOr404(id));
    }

    @Transactional(readOnly = true)
    public List<VariableCatalogEntryDTO> variableCatalog() {
        return variableCatalog.catalog();
    }

    // toda procuração precisa ter, obrigatoriamente, um OUTORGANTE e um OUTORGADO
    private void validateProcuracaoParties(CreateTemplateDTO dto) {
        if (dto.category() != TemplateCategory.PROCURACAO) {
            return;
        }
        boolean hasOutorgante = false;
        boolean hasOutorgado = false;
        if (dto.variables() != null) {
            for (TemplateVariableDTO v : dto.variables()) {
                if (v.partyRole() == PartyRole.OUTORGANTE) {
                    hasOutorgante = true;
                } else if (v.partyRole() == PartyRole.OUTORGADO) {
                    hasOutorgado = true;
                }
            }
        }
        if (!hasOutorgante || !hasOutorgado) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Toda procuração precisa ter uma variável marcada como OUTORGANTE e outra como OUTORGADO");
        }
    }

    private void validateVariables(CreateTemplateDTO dto) {
        if (dto.variables() == null) {
            return;
        }
        for (TemplateVariableDTO v : dto.variables()) {
            if (v.source() == VariableSource.MANUAL) {
                continue;
            }
            if (!variableCatalog.isValidAutoField(v.source(), v.sourceField())) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Campo inválido para a variável '" + v.key() + "': "
                                + v.source() + "/" + v.sourceField());
            }
        }
    }

    private DocumentTemplate findEntityOr404(Long id) {
        if (currentUser.isAdmin()) {
            return templateRepository.findById(id).orElseThrow(() -> notFound(id));
        }
        return templateRepository.findByIdAndOfficeId(id, currentUser.officeId()).orElseThrow(() -> notFound(id));
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Template não encontrado: " + id);
    }
}
