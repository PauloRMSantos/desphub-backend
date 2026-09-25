-- Chassi do veículo é opcional; passa a ser exigido apenas quando não há placa (validado na aplicação).
ALTER TABLE vehicle ALTER COLUMN chassis DROP NOT NULL;

-- Unicidade por escritório só para chassis preenchidos (permite vários veículos sem chassi).
DROP INDEX IF EXISTS ux_vehicle_office_chassis;
CREATE UNIQUE INDEX ux_vehicle_office_chassis
    ON vehicle (office_id, chassis)
    WHERE chassis IS NOT NULL;
