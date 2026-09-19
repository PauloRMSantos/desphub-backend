-- Escritório pode ser CPF (despachante autônomo) ou CNPJ (empresa).
ALTER TABLE office RENAME COLUMN cnpj TO cpf_cnpj;
ALTER TABLE office ALTER COLUMN cpf_cnpj TYPE VARCHAR(18);
