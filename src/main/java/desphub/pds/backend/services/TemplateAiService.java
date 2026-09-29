package desphub.pds.backend.services;

import desphub.pds.backend.dtos.templates.CreateTemplateDTO;
import desphub.pds.backend.dtos.templates.TemplateVariableDTO;
import desphub.pds.backend.dtos.templates.VariableCatalogEntryDTO;
import desphub.pds.backend.enums.TemplateCategory;
import desphub.pds.backend.enums.VariableSource;
import desphub.pds.backend.integrations.AiClient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@Service
public class TemplateAiService {

    private final AiClient aiClient;
    private final VariableCatalog variableCatalog;
    private final ObjectMapper objectMapper;

    public TemplateAiService(AiClient aiClient, VariableCatalog variableCatalog, ObjectMapper objectMapper) {
        this.aiClient = aiClient;
        this.variableCatalog = variableCatalog;
        this.objectMapper = objectMapper;
    }

    public CreateTemplateDTO fromText(String rawText, TemplateCategory category) {
        String user = "Estruture o documento abaixo em um template modular, preservando ao máximo a "
                + "redação original. Categoria: " + categoryOrDefault(category) + ".\n\n"
                + "DOCUMENTO:\n" + rawText;
        return generate(user);
    }

    public CreateTemplateDTO fromDescription(String description, TemplateCategory category) {
        String user = "Crie um rascunho de template a partir desta descrição. Categoria: "
                + categoryOrDefault(category) + ".\n\nDESCRIÇÃO:\n" + description;
        return generate(user);
    }

    private CreateTemplateDTO generate(String userPrompt) {
        String raw = aiClient.complete(systemPrompt(), userPrompt);
        String json = extractJson(raw);

        CreateTemplateDTO dto;
        try {
            dto = objectMapper.readValue(json, CreateTemplateDTO.class);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(BAD_GATEWAY, "A IA retornou um formato inválido. Tente novamente.");
        }
        return sanitize(dto);
    }

    private CreateTemplateDTO sanitize(CreateTemplateDTO dto) {
        List<TemplateVariableDTO> vars = new ArrayList<>();
        if (dto.variables() != null) {
            for (TemplateVariableDTO v : dto.variables()) {
                if (v.source() != null && v.source() != VariableSource.MANUAL
                        && !variableCatalog.isValidAutoField(v.source(), v.sourceField())) {
                    vars.add(new TemplateVariableDTO(v.key(), v.label(), VariableSource.MANUAL, null,
                            v.required(), v.partyRole()));
                } else {
                    vars.add(v);
                }
            }
        }
        TemplateCategory category = dto.category() == null ? TemplateCategory.PROCURACAO : dto.category();
        return new CreateTemplateDTO(dto.name(), category, dto.active(),
                dto.fixedBlocks(), dto.groups(), vars);
    }

    private String extractJson(String raw) {
        if (raw == null) {
            throw new ResponseStatusException(BAD_GATEWAY, "A IA não retornou conteúdo.");
        }
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new ResponseStatusException(BAD_GATEWAY, "A IA não retornou um JSON válido.");
        }
        return raw.substring(start, end + 1);
    }

    private TemplateCategory categoryOrDefault(TemplateCategory c) {
        return c == null ? TemplateCategory.PROCURACAO : c;
    }

    private String systemPrompt() {
        return """
                Você é um assistente que estrutura documentos de despachante brasileiro (procurações,
                declarações) em um TEMPLATE MODULAR em JSON.

                REGRAS:
                - Responda APENAS com um objeto JSON válido: sem markdown, sem comentários, sem texto fora do JSON.
                - Todo o texto do documento (campo "body") deve estar em português.
                - Use placeholders no formato {{chave}} dentro dos "body". Cada {{chave}} usada deve ter uma
                  entrada correspondente em "variables" com a mesma "key".
                - Para dados do sistema, use as variáveis do CATÁLOGO abaixo (com source e sourceField EXATOS).
                  Para qualquer dado que NÃO esteja no catálogo (ex.: número do motor, finalidade, valor da venda),
                  use source "MANUAL" e NÃO inclua sourceField.
                - Separe em "fixedBlocks" (partes que SEMPRE entram: abertura, fecho, cláusulas obrigatórias) e
                  "groups" (blocos OPCIONAIS que o usuário liga/desliga na hora de gerar).
                  * selectionType "SINGLE" = escolher UMA opção (ex.: transferir em nome do proprietário X para si).
                  * selectionType "MULTI"  = escolher VÁRIAS (ex.: poderes avulsos: dirigir, responder multas...).
                  * "required": true no grupo quando ao menos uma opção for obrigatória.
                - "sortOrder" define a ordem no documento final (menor primeiro). Use a abertura com valor baixo
                  (ex.: 0) e o fecho com valor alto (ex.: 1000).
                - "category" deve ser um de: PROCURACAO, DECLARACAO, OUTRO.
                - PARTES: em variables, "partyRole" indica o papel na procuração: "OUTORGANTE" (quem concede os
                  poderes, normalmente o cliente/proprietário) ou "OUTORGADO" (quem recebe, normalmente o
                  despachante/escritório). Use null para variáveis que não são de parte.
                - OBRIGATÓRIO para category "PROCURACAO": inclua ao menos UMA variável com partyRole "OUTORGANTE"
                  e UMA com partyRole "OUTORGADO" (tipicamente o nome de cada parte). Sem isso a procuração é
                  inválida. Para DECLARACAO/OUTRO, partyRole pode ser null.

                ESQUEMA (use exatamente estas chaves):
                {
                  "name": "string",
                  "category": "PROCURACAO",
                  "active": true,
                  "fixedBlocks": [ { "label": "string", "body": "string", "sortOrder": 0 } ],
                  "groups": [ {
                    "key": "string", "label": "string",
                    "selectionType": "SINGLE",
                    "required": false, "sortOrder": 10,
                    "blocks": [ { "label": "string", "body": "string", "sortOrder": 11, "defaultSelected": false } ]
                  } ],
                  "variables": [ { "key": "string", "label": "string", "source": "CLIENT", "sourceField": "name", "required": true, "partyRole": "OUTORGANTE" } ]
                }

                CATÁLOGO DE VARIÁVEIS AUTOMÁTICAS (só estas combinações source/sourceField são válidas):
                """
                + catalogText();
    }

    private String catalogText() {
        StringBuilder sb = new StringBuilder();
        for (VariableCatalogEntryDTO e : variableCatalog.catalog()) {
            sb.append("- {{").append(e.suggestedKey()).append("}} -> source=")
                    .append(e.source()).append(", sourceField=").append(e.sourceField())
                    .append("  (").append(e.label()).append(")\n");
        }
        return sb.toString();
    }
}
