-- Solo tests. SuperadminTrabajadorRepository lee identity_usuario, la tabla de rrhh-identity en la base
-- MySQL compartida del MVP. H2 no la tiene, así que se crea aquí con las columnas que usa la consulta.
-- Es un callback de Flyway (corre después de migrar) y no ocupa número de versión.
CREATE TABLE IF NOT EXISTS identity_usuario (
    id CHAR(36) NOT NULL PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    trabajador_id CHAR(36) NULL,
    email VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL
);

-- Tablas de rrhh-identity que lee este servicio (ADR-020): estado de empresa para el filtro de suspensión y
-- grupos de empresas para el alcance de plataforma. Solo tests; en la base compartida las crea identity.
CREATE TABLE IF NOT EXISTS identity_tenant (
    id CHAR(36) NOT NULL PRIMARY KEY,
    slug VARCHAR(80) NOT NULL,
    estado VARCHAR(30) NOT NULL
);
CREATE TABLE IF NOT EXISTS identity_grupo_tenant (
    grupo_id CHAR(36) NOT NULL,
    tenant_id CHAR(36) NOT NULL,
    PRIMARY KEY (grupo_id, tenant_id)
);
CREATE TABLE IF NOT EXISTS identity_grupo_operador (
    grupo_id CHAR(36) NOT NULL,
    usuario_id CHAR(36) NOT NULL,
    PRIMARY KEY (grupo_id, usuario_id)
);
