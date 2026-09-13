package com.rrhh.trabajadores.dto.response;

import java.math.BigDecimal;

public record TrabajadorResponse(
        String id,
        String tenantId,
        String nombre,
        String apellido,
        String rutTrabajador,
        String email,
        String telefono,
        String departamentoId,
        String cargoId,
        String jefaturaId,
        BigDecimal diasVacacionesDisponibles,
        boolean activo
) {}
