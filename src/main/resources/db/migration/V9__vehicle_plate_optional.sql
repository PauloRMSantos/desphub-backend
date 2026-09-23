-- Placa do veículo é opcional (veículos 0 km ainda não têm placa).
ALTER TABLE vehicle ALTER COLUMN plate DROP NOT NULL;

-- A unicidade global era bug multi-tenant (e barraria vazios). Passa a ser única
-- POR ESCRITÓRIO e só para placas preenchidas.
ALTER TABLE vehicle DROP CONSTRAINT IF EXISTS vehicle_plate_key;
CREATE UNIQUE INDEX ux_vehicle_office_plate
    ON vehicle (office_id, plate)
    WHERE plate IS NOT NULL;
