-- Despesas do módulo financeiro (isoladas por escritório).
CREATE TABLE expense (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    office_id    BIGINT        NOT NULL REFERENCES office (id),
    description  VARCHAR(255)  NOT NULL,
    amount       NUMERIC(12,2) NOT NULL,
    expense_date DATE          NOT NULL,
    category     VARCHAR(100)
);

CREATE INDEX ix_expense_office_date ON expense (office_id, expense_date);
