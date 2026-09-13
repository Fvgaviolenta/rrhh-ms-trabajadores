CREATE TABLE trabajadores_departamento (
    id CHAR(36) NOT NULL PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    nombre_departamento VARCHAR(255) NOT NULL
);

CREATE TABLE trabajadores_cargo (
    id CHAR(36) NOT NULL PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    nombre_cargo VARCHAR(255) NOT NULL
);

CREATE TABLE trabajadores_trabajador (
    id CHAR(36) NOT NULL PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    apellido VARCHAR(120) NOT NULL,
    rut_trabajador VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefono VARCHAR(40) NULL,
    departamento_id CHAR(36) NULL,
    cargo_id CHAR(36) NULL,
    jefatura_id CHAR(36) NULL,
    dias_vacaciones_disponibles DECIMAL(6,2) NOT NULL DEFAULT 15.00,
    fecha_desvinculacion DATETIME NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en DATETIME NOT NULL,
    CONSTRAINT uk_trabajador_tenant_rut UNIQUE (tenant_id, rut_trabajador),
    CONSTRAINT fk_trabajador_depto FOREIGN KEY (departamento_id) REFERENCES trabajadores_departamento (id),
    CONSTRAINT fk_trabajador_cargo FOREIGN KEY (cargo_id) REFERENCES trabajadores_cargo (id)
);

CREATE TABLE trabajadores_afiliacion_previsional (
    id CHAR(36) NOT NULL PRIMARY KEY,
    trabajador_id CHAR(36) NOT NULL,
    afp_id CHAR(36) NOT NULL,
    fecha_vigencia DATE NOT NULL,
    estado VARCHAR(30) NOT NULL,
    CONSTRAINT fk_afp_trabajador FOREIGN KEY (trabajador_id) REFERENCES trabajadores_trabajador (id)
);

CREATE TABLE trabajadores_afiliacion_salud (
    id CHAR(36) NOT NULL PRIMARY KEY,
    trabajador_id CHAR(36) NOT NULL,
    isapre_id CHAR(36) NULL,
    tipo_salud VARCHAR(40) NOT NULL,
    fecha_vigencia DATE NOT NULL,
    estado VARCHAR(30) NOT NULL,
    CONSTRAINT fk_salud_trabajador FOREIGN KEY (trabajador_id) REFERENCES trabajadores_trabajador (id)
);

CREATE INDEX idx_trabajador_tenant ON trabajadores_trabajador (tenant_id, activo);
