package com.rrhh.trabajadores.dto.request;

import java.math.BigDecimal;

public record ActualizarTrabajadorRequest(
        String nombre,
        String apellido,
        String email,
        String telefono,
        String departamentoId,
        String cargoId,
        String jefaturaId,
        BigDecimal diasVacacionesDisponibles
) {}
