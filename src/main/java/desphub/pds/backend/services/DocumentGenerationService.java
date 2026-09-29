package desphub.pds.backend.services;

import desphub.pds.backend.dtos.documents.GenerateDocumentDTO;
import desphub.pds.backend.dtos.documents.GeneratedDocumentResponseDTO;
import desphub.pds.backend.dtos.documents.GeneratedDocumentSummaryDTO;
import desphub.pds.backend.enums.SelectionType;
import desphub.pds.backend.enums.VariableSource;
import desphub.pds.backend.models.ClauseBlock;
import desphub.pds.backend.models.Client;
import desphub.pds.backend.models.DocumentTemplate;
import desphub.pds.backend.models.GeneratedDocument;
import desphub.pds.backend.models.Office;
import desphub.pds.backend.models.TemplateVariable;
import desphub.pds.backend.models.User;
import desphub.pds.backend.models.Vehicle;
import desphub.pds.backend.repositories.IClientRepository;
import desphub.pds.backend.repositories.IDocumentTemplateRepository;
import desphub.pds.backend.repositories.IGeneratedDocumentRepository;
import desphub.pds.backend.repositories.IOfficeRepository;
import desphub.pds.backend.repositories.IUserRepository;
import desphub.pds.backend.repositories.IVehicleRepository;
import desphub.pds.backend.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DocumentGenerationService {

    private final IDocumentTemplateRepository templateRepository;
    private final IGeneratedDocumentRepository generatedRepository;
    private final IClientRepository clientRepository;
    private final IVehicleRepository vehicleRepository;
    private final IOfficeRepository officeRepository;
    private final IUserRepository userRepository;
    private final VariableCatalog variableCatalog;
    private final DocumentPdfRenderer pdfRenderer;
    private final ObjectMapper objectMapper;
    private final CurrentUser currentUser;

    public DocumentGenerationService(IDocumentTemplateRepository templateRepository,
                                     IGeneratedDocumentRepository generatedRepository,
                                     IClientRepository clientRepository,
                                     IVehicleRepository vehicleRepository,
                                     IOfficeRepository officeRepository,
                                     IUserRepository userRepository,
                                     VariableCatalog variableCatalog,
                                     DocumentPdfRenderer pdfRenderer,
                                     ObjectMapper objectMapper,
                                     CurrentUser currentUser) {
        this.templateRepository = templateRepository;
        this.generatedRepository = generatedRepository;
        this.clientRepository = clientRepository;
        this.vehicleRepository = vehicleRepository;
        this.officeRepository = officeRepository;
        this.userRepository = userRepository;
        this.variableCatalog = variableCatalog;
        this.pdfRenderer = pdfRenderer;
        this.objectMapper = objectMapper;
        this.currentUser = currentUser;
    }

    @Transactional
    public GeneratedDocumentResponseDTO generate(GenerateDocumentDTO dto) {
        Long officeId = currentUser.requireOfficeId();

        DocumentTemplate template = templateRepository.findByIdAndOfficeId(dto.templateId(), officeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Template não encontrado: " + dto.templateId()));

        Client client = clientRepository.findByIdAndOfficeId(dto.clientId(), officeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente não encontrado: " + dto.clientId()));

        Vehicle vehicle = null;
        if (dto.vehicleId() != null) {
            vehicle = vehicleRepository.findByIdAndOfficeId(dto.vehicleId(), officeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Veículo não encontrado: " + dto.vehicleId()));
        }

        Office office = officeRepository.findById(officeId).orElse(null);
        User user = userRepository.findById(currentUser.get().userId()).orElse(null);

        Set<Long> selected = new HashSet<>(dto.selectedBlockIds() == null ? List.of() : dto.selectedBlockIds());

        validateSelection(template, selected);

        String assembled = assemble(template, selected);
        Map<String, String> resolved = resolveVariables(template, client, vehicle, office, user, dto.manualValues());
        String content = substitute(assembled, resolved);

        GeneratedDocument gd = new GeneratedDocument();
        gd.setOfficeId(officeId);
        gd.setTemplateId(template.getId());
        gd.setTemplateName(template.getName());
        gd.setClientId(client.getId());
        gd.setClientName(client.getName());
        gd.setVehicleId(vehicle == null ? null : vehicle.getId());
        gd.setResolvedContent(content);
        gd.setSelectedBlockIds(selected.isEmpty() ? null
                : selected.stream().map(String::valueOf).collect(Collectors.joining(",")));
        gd.setFilledValues(toJson(dto.manualValues()));
        gd.setCreatedByUserId(currentUser.get().userId());

        return toResponse(generatedRepository.save(gd));
    }

    @Transactional(readOnly = true)
    public List<GeneratedDocumentSummaryDTO> list(Long clientId) {
        List<GeneratedDocument> docs = clientId != null
                ? generatedRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                : generatedRepository.findAllByOrderByCreatedAtDesc();
        return docs.stream()
                .map(d -> new GeneratedDocumentSummaryDTO(d.getId(), d.getTemplateName(),
                        d.getClientName(), d.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public GeneratedDocumentResponseDTO getById(Long id) {
        return toResponse(findEntityOr404(id));
    }

    @Transactional(readOnly = true)
    public byte[] pdf(Long id) {
        return pdfRenderer.render(findEntityOr404(id).getResolvedContent());
    }

    private void validateSelection(DocumentTemplate template, Set<Long> selected) {
        Map<Long, ClauseBlock> byId = template.getBlocks().stream()
                .collect(Collectors.toMap(ClauseBlock::getId, b -> b));

        for (Long id : selected) {
            ClauseBlock b = byId.get(id);
            if (b == null) {
                throw unprocessable("Bloco não pertence a este template: " + id);
            }
            if (b.getGroupKey() == null) {
                throw unprocessable("Bloco fixo não pode ser selecionado: " + id);
            }
        }

        Map<String, List<ClauseBlock>> groups = template.getBlocks().stream()
                .filter(b -> b.getGroupKey() != null)
                .collect(Collectors.groupingBy(ClauseBlock::getGroupKey, LinkedHashMap::new, Collectors.toList()));

        for (List<ClauseBlock> gblocks : groups.values()) {
            ClauseBlock head = gblocks.get(0);
            long count = gblocks.stream().filter(b -> selected.contains(b.getId())).count();
            String name = head.getGroupLabel() != null ? head.getGroupLabel() : head.getGroupKey();
            if (head.getSelectionType() == SelectionType.SINGLE && count > 1) {
                throw unprocessable("Escolha apenas uma opção em '" + name + "'");
            }
            if (head.isGroupRequired() && count < 1) {
                throw unprocessable("Escolha uma opção em '" + name + "'");
            }
        }
    }

    private String assemble(DocumentTemplate template, Set<Long> selected) {
        List<ClauseBlock> included = new ArrayList<>(template.getBlocks().stream()
                .filter(b -> b.getGroupKey() == null || selected.contains(b.getId()))
                .toList());
        included.sort((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()));
        return included.stream().map(ClauseBlock::getBody).collect(Collectors.joining("\n\n"));
    }

    private Map<String, String> resolveVariables(DocumentTemplate template, Client client, Vehicle vehicle,
                                                 Office office, User user, Map<String, String> manualValues) {
        Map<String, String> manual = manualValues == null ? Map.of() : manualValues;
        Map<String, String> resolved = new HashMap<>();

        for (TemplateVariable v : template.getVariables()) {
            String value;
            if (v.getSource() == VariableSource.MANUAL) {
                value = manual.get(v.getVarKey());
                if (v.isRequired() && isBlank(value)) {
                    throw unprocessable("Preencha o campo: " + v.getLabel());
                }
            } else if (v.getSource() == VariableSource.VEHICLE && vehicle == null) {
                if (v.isRequired()) {
                    throw unprocessable("Selecione um veículo para preencher: " + v.getLabel());
                }
                value = "";
            } else {
                value = variableCatalog.resolveAuto(v.getSource(), v.getSourceField(),
                        client, vehicle, office, user);
                if (v.isRequired() && isBlank(value)) {
                    throw unprocessable("Sem valor disponível para: " + v.getLabel());
                }
            }
            resolved.put(v.getVarKey(), value == null ? "" : value);
        }
        return resolved;
    }

    private String substitute(String text, Map<String, String> vars) {
        String result = text;
        for (Map.Entry<String, String> e : vars.entrySet()) {
            String pattern = "\\{\\{\\s*" + Pattern.quote(e.getKey()) + "\\s*\\}\\}";
            result = result.replaceAll(pattern, Matcher.quoteReplacement(e.getValue()));
        }
        return result;
    }

    private GeneratedDocument findEntityOr404(Long id) {
        return generatedRepository.findByIdAndOfficeId(id, currentUser.requireOfficeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Documento não encontrado: " + id));
    }

    private GeneratedDocumentResponseDTO toResponse(GeneratedDocument d) {
        return new GeneratedDocumentResponseDTO(d.getId(), d.getTemplateId(), d.getTemplateName(),
                d.getClientId(), d.getClientName(), d.getVehicleId(), d.getCreatedAt(), d.getResolvedContent());
    }

    private String toJson(Map<String, String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return objectMapper.writeValueAsString(values);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static ResponseStatusException unprocessable(String message) {
        return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
