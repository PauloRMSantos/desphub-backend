-- Seed do administrador da plataforma (DESPHUB_ADMIN). Não pertence a nenhum escritório.
-- Senha padrão: "admin123" (hash BCrypt abaixo) — TROQUE em produção.
-- Idempotente: só cria se ainda não existir um admin.
INSERT INTO app_user (name, email, password_hash, role, active)
SELECT 'DespHub Admin',
       'admin@desphub.com',
       '$2a$10$yfGdF8DeF9oE6CyDD8Y1tu/TefWORyGvd1lhjViZaPTUup9wJ9MiO',
       'DESPHUB_ADMIN',
       TRUE
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE role = 'DESPHUB_ADMIN');
