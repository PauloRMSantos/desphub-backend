-- Onda 1: fundação de multi-tenancy (escritórios) e usuários.

CREATE TABLE office (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    cnpj       VARCHAR(14)  NOT NULL UNIQUE,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- "user" é palavra reservada no Postgres, por isso a tabela é app_user.
CREATE TABLE app_user (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    office_id     BIGINT       REFERENCES office (id),  -- nulo para o DESPHUB_ADMIN
    name          VARCHAR(150) NOT NULL,
    email         VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(30)  NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE user_permission (
    user_id    BIGINT      NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    permission VARCHAR(40) NOT NULL,
    PRIMARY KEY (user_id, permission)
);
