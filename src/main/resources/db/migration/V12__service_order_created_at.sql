-- Data de criação da OS (para a Receita filtrar por mês). Linhas existentes recebem now().
ALTER TABLE service_order ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT now();
