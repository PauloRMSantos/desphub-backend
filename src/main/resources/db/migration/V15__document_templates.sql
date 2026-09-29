-- Fase 1: templates modulares de documentos (procuração/declaração) por escritório.
-- Só as raízes (document_template, generated_document) carregam office_id + @Filter;
-- os filhos são alcançados via template (padrão budget/budget_item).

CREATE TABLE document_template (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    office_id          BIGINT       NOT NULL REFERENCES office (id),
    name               VARCHAR(200) NOT NULL,
    category           VARCHAR(40)  NOT NULL,       -- PROCURACAO / DECLARACAO / OUTRO
    active             BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by_user_id BIGINT       REFERENCES app_user (id),
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX ix_document_template_office ON document_template (office_id);

-- Bloco de cláusula. group_key nulo = bloco FIXO (sempre entra: abertura, fecho…).
-- group_key preenchido = bloco OPCIONAL; metadados do grupo ficam achatados aqui
-- (mesmo group_key => mesmo group_label/selection_type/group_required/group_sort_order).
CREATE TABLE clause_block (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    template_id      BIGINT       NOT NULL REFERENCES document_template (id) ON DELETE CASCADE,
    label            VARCHAR(200) NOT NULL,
    body             TEXT         NOT NULL,
    sort_order       INTEGER      NOT NULL DEFAULT 0,   -- posição no documento montado
    default_selected BOOLEAN      NOT NULL DEFAULT FALSE,
    group_key        VARCHAR(80),
    group_label      VARCHAR(200),
    selection_type   VARCHAR(20),                       -- SINGLE / MULTI
    group_required   BOOLEAN      NOT NULL DEFAULT FALSE,
    group_sort_order INTEGER      NOT NULL DEFAULT 0
);
CREATE INDEX ix_clause_block_template ON clause_block (template_id);

CREATE TABLE template_variable (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    template_id  BIGINT       NOT NULL REFERENCES document_template (id) ON DELETE CASCADE,
    var_key      VARCHAR(100) NOT NULL,     -- token usado no corpo: {{var_key}}
    label        VARCHAR(200) NOT NULL,
    source       VARCHAR(20)  NOT NULL,     -- CLIENT/VEHICLE/OFFICE/USER/MANUAL
    source_field VARCHAR(60),               -- nulo para MANUAL
    required     BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_template_variable_template ON template_variable (template_id);

-- Documento gerado por cliente. Guarda snapshots (template_name/client_name) e o texto
-- resolvido para reimpressão, sobrevivendo a edições/exclusões do template.
CREATE TABLE generated_document (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    office_id          BIGINT       NOT NULL REFERENCES office (id),
    template_id        BIGINT       REFERENCES document_template (id) ON DELETE SET NULL,
    template_name      VARCHAR(200) NOT NULL,
    client_id          BIGINT       REFERENCES client (id) ON DELETE SET NULL,
    client_name        VARCHAR(255),
    vehicle_id         BIGINT       REFERENCES vehicle (id) ON DELETE SET NULL,
    resolved_content   TEXT         NOT NULL,
    selected_block_ids VARCHAR(500),
    filled_values      TEXT,
    created_by_user_id BIGINT       REFERENCES app_user (id),
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX ix_generated_document_office ON generated_document (office_id);
CREATE INDEX ix_generated_document_client ON generated_document (office_id, client_id);
