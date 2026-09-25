-- Comporta CNPJ formatado (ex.: "01.864.805/0001-46" = 18 chars), igual ao office (V5).
-- Antes era VARCHAR(14), que estourava com CNPJ (formatado ou não com máscara).
ALTER TABLE client ALTER COLUMN cpf_cnpj TYPE VARCHAR(18);
