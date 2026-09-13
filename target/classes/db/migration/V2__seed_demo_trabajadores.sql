INSERT INTO trabajadores_departamento (id, tenant_id, nombre_departamento) VALUES
    ('dddddddd-dddd-dddd-dddd-dddddddd0001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Operaciones');

INSERT INTO trabajadores_cargo (id, tenant_id, nombre_cargo) VALUES
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeee0001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Analista RRHH');

INSERT INTO trabajadores_trabajador (
    id, tenant_id, nombre, apellido, rut_trabajador, email, telefono,
    departamento_id, cargo_id, jefatura_id, dias_vacaciones_disponibles, activo, creado_en
) VALUES (
    'ffffffff-ffff-ffff-ffff-ffffffffffff',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'Ana', 'Pérez', '12.345.678-9', 'ana.perez@rrhh.local', '+56911111111',
    'dddddddd-dddd-dddd-dddd-dddddddd0001',
    'eeeeeeee-eeee-eeee-eeee-eeeeeeee0001',
    NULL, 15.00, TRUE, CURRENT_TIMESTAMP
);
