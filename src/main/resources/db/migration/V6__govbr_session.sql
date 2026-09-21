-- Estado da conexão gov.br por escritório (espelho local; o RPA guarda a sessão real).
CREATE TABLE govbr_session (
    office_id  BIGINT PRIMARY KEY REFERENCES office (id),
    connected  BOOLEAN     NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
