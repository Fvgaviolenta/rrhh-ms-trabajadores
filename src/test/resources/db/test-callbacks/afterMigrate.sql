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
