# Templates de Documentos (Fases 1 e 2)

Modelos de documentos (procuração, declaração…) **por escritório**, criados pelo próprio
despachante, montados de forma **modular** (blocos fixos + grupos de blocos opcionais) e
gerados por cliente com **substituição de variáveis** (sem IA, custo zero). Este documento é o
contrato para o frontend.

> **Fase 1** = núcleo determinístico (autoria manual, geração, histórico, PDF).
> **Fase 2** = IA como assistente de autoria (dois on-ramps que devolvem uma *sugestão* de
> template, não persistida). Ver a seção "Fase 2 — IA" no fim.

---

## Conceito

Um **template** é uma lista de blocos:

- **Bloco fixo** — sempre entra no documento (abertura, fecho, cláusula obrigatória).
- **Bloco opcional** — pertence a um **grupo**; é ligado/desligado na geração.
  - Grupo `SINGLE` = escolha um (radio); `MULTI` = escolha vários (checkbox).
  - Grupo pode ser `required` (precisa escolher ao menos um).

O corpo dos blocos usa **placeholders** `{{chave}}`. Cada template declara suas **variáveis**,
dizendo de onde vem o valor de cada chave (`CLIENT`, `VEHICLE`, `OFFICE`, `USER`, `MANUAL`).

Na **geração**, o backend valida as regras de grupo, resolve as variáveis (auto do banco +
manuais preenchidas na hora), monta os blocos por `sortOrder`, substitui os `{{...}}` e grava um
**documento gerado** (com snapshot do texto para **reimpressão** em PDF).

## Permissões

| Permissão | Uso |
|---|---|
| `TEMPLATES_READ` / `TEMPLATES_WRITE` | ver / autorar templates |
| `DOCUMENTS_READ` / `DOCUMENTS_WRITE` | ver·reimprimir / gerar documentos |

Owner/admin do escritório recebem todas automaticamente; funcionários só as concedidas.

---

## Endpoints

### Autoria de templates

`POST /api/templates` · `PUT /api/templates/{id}` — cria/edita (mesmo payload). `TEMPLATES_WRITE`

```json
{
  "name": "Procuração de Transferência",
  "category": "PROCURACAO",              // PROCURACAO | DECLARACAO | OUTRO
  "active": true,                         // opcional, default true
  "fixedBlocks": [
    { "label": "Abertura", "body": "OUTORGANTE: {{cliente.nome}} ... placa {{veiculo.placa}}:", "sortOrder": 0 },
    { "label": "Fecho",    "body": "{{local}}, {{data}}.\n\n___\n{{cliente.nome}}",              "sortOrder": 100 }
  ],
  "groups": [
    {
      "key": "transferencia", "label": "Transferência",
      "selectionType": "SINGLE", "required": true, "sortOrder": 10,
      "blocks": [
        { "label": "Em nome do proprietário", "body": "- transferir em nome do proprietário;", "sortOrder": 11 },
        { "label": "Para si (procurador)",     "body": "- transferir para o procurador;",       "sortOrder": 12 }
      ]
    },
    {
      "key": "poderes", "label": "Poderes gerais",
      "selectionType": "MULTI", "required": false, "sortOrder": 20,
      "blocks": [
        { "label": "Dirigir",           "body": "- conduzir o veículo;",              "sortOrder": 21, "defaultSelected": true },
        { "label": "Responder multas",  "body": "- responder por multas;",           "sortOrder": 22 }
      ]
    }
  ],
  "variables": [
    { "key": "cliente.nome",  "label": "Nome do cliente", "source": "CLIENT",  "sourceField": "name",  "required": true },
    { "key": "veiculo.placa", "label": "Placa",           "source": "VEHICLE", "sourceField": "plate", "required": true },
    { "key": "local",         "label": "Local",           "source": "MANUAL",                          "required": true },
    { "key": "data",          "label": "Data",            "source": "MANUAL",                          "required": true }
  ]
}
```

- `sortOrder` (bloco/grupo), `defaultSelected`, `required`, `active` são **opcionais** (default
  `0` / `false` / `false` / `true`). O `sortOrder` do bloco define a **posição no documento**.
- Resposta é o template completo com os **ids dos blocos** (necessários para gerar).

Outros:
- `GET /api/templates?category=PROCURACAO` → lista resumida (`TEMPLATES_READ`).
- `GET /api/templates/{id}` → template completo (`TEMPLATES_READ`).
- `DELETE /api/templates/{id}` → 204 (`TEMPLATES_WRITE`).
- `GET /api/templates/variable-catalog` → variáveis automáticas disponíveis (para o editor):
  ```json
  [{ "source": "CLIENT", "sourceField": "name", "suggestedKey": "cliente.nome", "label": "Nome do cliente" }, ...]
  ```

**Campos automáticos** (source → sourceField): `CLIENT` → name, cpfCnpj, telephone, address ·
`VEHICLE` → plate, brand, model, fabricationAndModel, color, renavam, chassis · `OFFICE` → name,
cpfCnpj · `USER` → name, email. `MANUAL` não usa `sourceField` (preenchido na geração).

**Partes da procuração (`partyRole`)** — cada variável pode ter `partyRole`: `OUTORGANTE` (quem
concede os poderes, ex.: o cliente), `OUTORGADO` (quem recebe, ex.: o escritório) ou `null`
(variável comum). **Regra obrigatória**: um template de categoria `PROCURACAO` só é salvo se tiver
ao menos uma variável `OUTORGANTE` **e** uma `OUTORGADO` — senão retorna `422` ("Toda procuração
precisa ter uma variável marcada como OUTORGANTE e outra como OUTORGADO"). Para `DECLARACAO`/`OUTRO`
a regra não se aplica. No editor, marque tipicamente o *nome* de cada parte com o papel.

### Geração e histórico

`POST /api/documents/generate` — gera e salva. `DOCUMENTS_WRITE`

```json
{
  "templateId": 1,
  "clientId": 1,
  "vehicleId": 1,                       // opcional (necessário se houver variável VEHICLE required)
  "selectedBlockIds": [3, 5],           // ids dos blocos OPCIONAIS escolhidos
  "manualValues": { "local": "Torres/RS", "data": "27/09/2026" }
}
```

Resposta `201` inclui `resolvedContent` (texto final já substituído) + snapshots
(`templateName`, `clientName`). Blocos fixos entram sozinhos — só se passa os opcionais.

Erros `422` (mensagem em `message`): grupo `SINGLE` com mais de um selecionado; grupo `required`
sem seleção; variável `MANUAL` obrigatória vazia; variável `VEHICLE` obrigatória sem veículo.

- `GET /api/documents?clientId=1` → histórico (resumo), mais recentes primeiro (`DOCUMENTS_READ`).
- `GET /api/documents/{id}` → detalhe com `resolvedContent` (`DOCUMENTS_READ`).
- `GET /api/documents/{id}/pdf` → **PDF** (reimpressão; regenerado do texto salvo). `DOCUMENTS_READ`

---

## Notas de implementação

- **Placeholders**: `{{chave}}` (espaços opcionais: `{{ chave }}`). Chaves sem valor viram vazio;
  placeholders sem variável declarada permanecem no texto (erro visível para o autor).
- **Template sempre editável** (o `active` só separa rascunho de usável, nunca trava edição). O
  histórico guarda o texto resolvido, então editar/excluir o template não altera documentos já
  gerados.
- **Multi-tenant**: tudo por `office_id` (`@Filter`); testado — office B recebe 404/lista vazia
  para dados do office A.
- **PDF (Fase 1)**: A4, texto puro com parágrafos (PDFBox). Formatação rica fica para depois.
- Migrations: `V15__document_templates.sql` (tabelas `document_template`, `clause_block`,
  `template_variable`, `generated_document`) e `V16__template_variable_party_role.sql` (coluna
  `party_role` para OUTORGANTE/OUTORGADO).

---

## Fase 2 — IA (assistente de autoria)

Dois on-ramps para **criar um template** com ajuda de IA. Os dois **retornam um `CreateTemplateDTO`
de SUGESTÃO — não salvam nada**. O frontend carrega a sugestão no mesmo editor da Fase 1, o
despachante revisa/ajusta e salva pelo `POST /api/templates`. (Provedor: Claude Haiku 4.5.)

`POST /api/templates/ai/from-text` — estrutura o modelo que o escritório **já usa**. `TEMPLATES_WRITE`
```json
{ "rawText": "PROCURAÇÃO ... (cole aqui o documento atual) ...", "category": "PROCURACAO" }
```

`POST /api/templates/ai/from-description` — gera um rascunho a partir de uma descrição. `TEMPLATES_WRITE`
```json
{ "description": "procuração de transferência com poder de dirigir e responder multas", "category": "PROCURACAO" }
```

- `category` é **opcional** (default `PROCURACAO`); só ajuda a IA.
- Resposta `200` = um `CreateTemplateDTO` (mesmo formato do corpo de `POST /api/templates`), pronto
  para revisão. A IA é instruída a usar só as variáveis do catálogo; qualquer campo automático
  inválido é **rebaixado para `MANUAL`** antes de devolver (a sugestão nunca vem quebrada).
- **Fluxo no front**: chamar o on-ramp → carregar o DTO no editor → deixar o usuário conferir →
  `POST /api/templates` para salvar. A IA nunca gera documento assinável direto; a aprovação humana
  (salvar) é a salvaguarda.

Erros: `503` se a IA não estiver configurada (sem chave); `502` se a IA responder num formato
inesperado (tentar de novo); `400` se `rawText`/`description` vierem vazios.

**Config** (`application.properties` / env no deploy): `desphub.ai.anthropic.api-key` (obrigatória
p/ ligar a IA), `desphub.ai.anthropic.model` (default `claude-haiku-4-5-20251001`),
`desphub.ai.anthropic.base-url` (default `https://api.anthropic.com`). Sem a chave, as rotas
`/ai/*` respondem 503 e o resto do sistema funciona normalmente. Provedor fica atrás da interface
`AiClient` (troca de provedor sem mexer no resto).
