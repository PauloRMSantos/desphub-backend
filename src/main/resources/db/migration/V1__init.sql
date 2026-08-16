CREATE TABLE client (
                        id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        name      VARCHAR(255) NOT NULL,
                        telephone VARCHAR(20)  NOT NULL,
                        cpf_cnpj  VARCHAR(14)  NOT NULL UNIQUE,
                        address   VARCHAR(255)
);

CREATE TABLE vehicle (
                         id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         plate                 VARCHAR(7)  NOT NULL UNIQUE,
                         brand                 VARCHAR(50) NOT NULL,
                         model                 VARCHAR(50) NOT NULL,
                         fabrication_and_model VARCHAR(20) NOT NULL,
                         color                 VARCHAR(30) NOT NULL,
                         renavam               VARCHAR(11),
                         chassis               VARCHAR(17) NOT NULL UNIQUE,
                         client_id             BIGINT,
                         CONSTRAINT fk_vehicle_client FOREIGN KEY (client_id) REFERENCES client (id)
);

CREATE TABLE service (
                         id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         name          VARCHAR(100)  NOT NULL UNIQUE,
                         category      VARCHAR(20)   NOT NULL DEFAULT 'SERVICO',
                         default_price NUMERIC(12,2) NOT NULL
);

CREATE TABLE budget (
                        id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        code        VARCHAR(20)   NOT NULL UNIQUE,
                        status      VARCHAR(20)   NOT NULL,
                        client_id   BIGINT,
                        total_price NUMERIC(12,2) NOT NULL DEFAULT 0,
                        CONSTRAINT fk_budget_client FOREIGN KEY (client_id) REFERENCES client (id)
);

CREATE TABLE budget_item (
                             id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             budget_id  BIGINT        NOT NULL,
                             service_id BIGINT        NOT NULL,
                             quantity   INTEGER       NOT NULL DEFAULT 1,
                             unit_price NUMERIC(12,2) NOT NULL,
                             CONSTRAINT fk_bi_budget  FOREIGN KEY (budget_id)  REFERENCES budget (id) ON DELETE CASCADE,
                             CONSTRAINT fk_bi_service FOREIGN KEY (service_id) REFERENCES service (id)
);

CREATE TABLE service_order (
                               id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               code             VARCHAR(20)   NOT NULL UNIQUE,
                               status           VARCHAR(30)   NOT NULL,
                               client_id        BIGINT        NOT NULL,
                               vehicle_id       BIGINT        NOT NULL,
                               origin_budget_id BIGINT,
                               services_total   NUMERIC(12,2) NOT NULL DEFAULT 0,
                               fees_total       NUMERIC(12,2) NOT NULL DEFAULT 0,
                               total            NUMERIC(12,2) NOT NULL DEFAULT 0,
                               CONSTRAINT fk_os_client  FOREIGN KEY (client_id)        REFERENCES client (id),
                               CONSTRAINT fk_os_vehicle FOREIGN KEY (vehicle_id)       REFERENCES vehicle (id),
                               CONSTRAINT fk_os_budget  FOREIGN KEY (origin_budget_id) REFERENCES budget (id)
);

CREATE TABLE service_order_item (
                                    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                    service_order_id BIGINT        NOT NULL,
                                    service_type_id  BIGINT        NOT NULL,
                                    quantity         INTEGER       NOT NULL DEFAULT 1,
                                    unit_price       NUMERIC(12,2) NOT NULL,
                                    CONSTRAINT fk_osi_order   FOREIGN KEY (service_order_id) REFERENCES service_order (id) ON DELETE CASCADE,
                                    CONSTRAINT fk_osi_service FOREIGN KEY (service_type_id)  REFERENCES service (id)
);