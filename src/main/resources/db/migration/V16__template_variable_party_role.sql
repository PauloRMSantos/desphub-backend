-- Papel da variável na procuração (OUTORGANTE/OUTORGADO). Nulo para variáveis sem papel de parte.
-- Toda procuração passa a exigir ao menos uma variável OUTORGANTE e uma OUTORGADO (validado na app).
ALTER TABLE template_variable ADD COLUMN party_role VARCHAR(20);
