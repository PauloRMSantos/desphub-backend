-- Nenhuma informação de negócio é global: toda unicidade passa a ser POR ESCRITÓRIO.
-- (office.cpf_cnpj e app_user.email seguem globais de propósito: identidade do tenant e do login.)

ALTER TABLE vehicle DROP CONSTRAINT IF EXISTS vehicle_chassis_key;
CREATE UNIQUE INDEX ux_vehicle_office_chassis ON vehicle (office_id, chassis);

ALTER TABLE service DROP CONSTRAINT IF EXISTS service_name_key;
CREATE UNIQUE INDEX ux_service_office_name ON service (office_id, name);

ALTER TABLE budget DROP CONSTRAINT IF EXISTS budget_code_key;
CREATE UNIQUE INDEX ux_budget_office_code ON budget (office_id, code);

ALTER TABLE service_order DROP CONSTRAINT IF EXISTS service_order_code_key;
CREATE UNIQUE INDEX ux_service_order_office_code ON service_order (office_id, code);
