-- Sessão gov.br por escritório, enviada ao RPA por requisição (multi-tenant).
-- bearer/user_id são criptografados na camada da aplicação (por isso TEXT).
ALTER TABLE govbr_session ADD COLUMN bearer  TEXT;
ALTER TABLE govbr_session ADD COLUMN user_id TEXT;
