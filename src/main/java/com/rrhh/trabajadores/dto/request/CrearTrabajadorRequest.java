package com.rrhh.trabajadores.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CrearTrabajadorRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank String rutTrabajador,
        @NotBlank @Email String email,
        String telefono,
        String departamentoId,
        String cargoId,
        String jefaturaId,
        BigDecimal diasVacacionesDisponibles
) {}
