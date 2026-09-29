package desphub.pds.backend.mappers;

import desphub.pds.backend.dtos.templates.CreateTemplateDTO;
import desphub.pds.backend.dtos.templates.TemplateBlockDTO;
import desphub.pds.backend.dtos.templates.TemplateGroupDTO;
import desphub.pds.backend.dtos.templates.TemplateResponseDTO;
import desphub.pds.backend.dtos.templates.TemplateSummaryDTO;
import desphub.pds.backend.dtos.templates.TemplateVariableDTO;
import desphub.pds.backend.models.ClauseBlock;
import desphub.pds.backend.models.DocumentTemplate;
import desphub.pds.backend.models.TemplateVariable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class TemplateMapper {

    public void apply(CreateTemplateDTO dto, DocumentTemplate entity) {
        entity.setName(dto.name());
        entity.setCategory(dto.category());
        entity.setActive(dto.active() == null || dto.active());

        entity.getBlocks().clear();
        entity.getVariables().clear();

        if (dto.fixedBlocks() != null) {
            for (TemplateBlockDTO b : dto.fixedBlocks()) {
                ClauseBlock block = new ClauseBlock();
                block.setLabel(b.label());
                block.setBody(b.body());
                block.setSortOrder(intOrZero(b.sortOrder()));
                block.setDefaultSelected(false);
                entity.addBlock(block);
            }
        }

        if (dto.groups() != null) {
            for (TemplateGroupDTO g : dto.groups()) {
                for (TemplateBlockDTO b : g.blocks()) {
                    ClauseBlock block = new ClauseBlock();
                    block.setLabel(b.label());
                    block.setBody(b.body());
                    block.setSortOrder(intOrZero(b.sortOrder()));
                    block.setDefaultSelected(Boolean.TRUE.equals(b.defaultSelected()));
                    block.setGroupKey(g.key());
                    block.setGroupLabel(g.label());
                    block.setSelectionType(g.selectionType());
                    block.setGroupRequired(Boolean.TRUE.equals(g.required()));
                    block.setGroupSortOrder(intOrZero(g.sortOrder()));
                    entity.addBlock(block);
                }
            }
        }

        if (dto.variables() != null) {
            for (TemplateVariableDTO v : dto.variables()) {
                TemplateVariable variable = new TemplateVariable();
                variable.setVarKey(v.key());
                variable.setLabel(v.label());
                variable.setSource(v.source());
                variable.setSourceField(v.sourceField());
                variable.setRequired(Boolean.TRUE.equals(v.required()));
                variable.setPartyRole(v.partyRole());
                entity.addVariable(variable);
            }
        }
    }

    private static int intOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    public TemplateResponseDTO toResponse(DocumentTemplate e) {
        List<TemplateBlockDTO> fixedBlocks = e.getBlocks().stream()
                .filter(b -> b.getGroupKey() == null)
                .sorted(Comparator.comparingInt(ClauseBlock::getSortOrder))
                .map(this::toBlockDTO)
                .toList();

        Map<String, List<ClauseBlock>> byGroup = new LinkedHashMap<>();
        for (ClauseBlock b : e.getBlocks()) {
            if (b.getGroupKey() != null) {
                byGroup.computeIfAbsent(b.getGroupKey(), k -> new ArrayList<>()).add(b);
            }
        }

        List<TemplateGroupDTO> groups = byGroup.values().stream()
                .map(blocks -> {
                    ClauseBlock head = blocks.get(0);
                    List<TemplateBlockDTO> blockDtos = blocks.stream()
                            .sorted(Comparator.comparingInt(ClauseBlock::getSortOrder))
                            .map(this::toBlockDTO)
                            .toList();
                    return new TemplateGroupDTO(
                            head.getGroupKey(),
                            head.getGroupLabel(),
                            head.getSelectionType(),
                            head.isGroupRequired(),
                            head.getGroupSortOrder(),
                            blockDtos);
                })
                .sorted(Comparator.comparingInt(TemplateGroupDTO::sortOrder)
                        .thenComparing(TemplateGroupDTO::key))
                .toList();

        List<TemplateVariableDTO> variables = e.getVariables().stream()
                .map(v -> new TemplateVariableDTO(v.getVarKey(), v.getLabel(), v.getSource(),
                        v.getSourceField(), v.isRequired(), v.getPartyRole()))
                .toList();

        return new TemplateResponseDTO(e.getId(), e.getName(), e.getCategory(), e.isActive(),
                e.getCreatedAt(), e.getUpdatedAt(), fixedBlocks, groups, variables);
    }

    public TemplateSummaryDTO toSummary(DocumentTemplate e) {
        return new TemplateSummaryDTO(e.getId(), e.getName(), e.getCategory(), e.isActive(), e.getUpdatedAt());
    }

    private TemplateBlockDTO toBlockDTO(ClauseBlock b) {
        return new TemplateBlockDTO(b.getId(), b.getLabel(), b.getBody(), b.getSortOrder(), b.isDefaultSelected());
    }
}
