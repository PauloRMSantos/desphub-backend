-- CPF/CNPJ do cliente é opcional (só nome e telefone são obrigatórios).
ALTER TABLE client ALTER COLUMN cpf_cnpj DROP NOT NULL;

-- A unicidade global era um bug multi-tenant (dois escritórios não poderiam ter o mesmo CPF)
-- e barraria vazios. Passa a ser única POR ESCRITÓRIO e só para valores preenchidos.
ALTER TABLE client DROP CONSTRAINT IF EXISTS client_cpf_cnpj_key;
CREATE UNIQUE INDEX ux_client_office_cpf_cnpj
    ON client (office_id, cpf_cnpj)
    WHERE cpf_cnpj IS NOT NULL;
