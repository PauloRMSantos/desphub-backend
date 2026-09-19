-- Onda 4: isolamento multi-tenant. Cada registro de negócio pertence a um escritório.

ALTER TABLE client        ADD COLUMN office_id BIGINT REFERENCES office (id);
ALTER TABLE vehicle       ADD COLUMN office_id BIGINT REFERENCES office (id);
ALTER TABLE service       ADD COLUMN office_id BIGINT REFERENCES office (id);
ALTER TABLE budget        ADD COLUMN office_id BIGINT REFERENCES office (id);
ALTER TABLE service_order ADD COLUMN office_id BIGINT REFERENCES office (id);

-- backfill de dados legados (dev) para o primeiro escritório, se existir algum
UPDATE client        SET office_id = (SELECT MIN(id) FROM office) WHERE office_id IS NULL AND EXISTS (SELECT 1 FROM office);
UPDATE vehicle       SET office_id = (SELECT MIN(id) FROM office) WHERE office_id IS NULL AND EXISTS (SELECT 1 FROM office);
UPDATE service       SET office_id = (SELECT MIN(id) FROM office) WHERE office_id IS NULL AND EXISTS (SELECT 1 FROM office);
UPDATE budget        SET office_id = (SELECT MIN(id) FROM office) WHERE office_id IS NULL AND EXISTS (SELECT 1 FROM office);
UPDATE service_order SET office_id = (SELECT MIN(id) FROM office) WHERE office_id IS NULL AND EXISTS (SELECT 1 FROM office);

ALTER TABLE client        ALTER COLUMN office_id SET NOT NULL;
ALTER TABLE vehicle       ALTER COLUMN office_id SET NOT NULL;
ALTER TABLE service       ALTER COLUMN office_id SET NOT NULL;
ALTER TABLE budget        ALTER COLUMN office_id SET NOT NULL;
ALTER TABLE service_order ALTER COLUMN office_id SET NOT NULL;
