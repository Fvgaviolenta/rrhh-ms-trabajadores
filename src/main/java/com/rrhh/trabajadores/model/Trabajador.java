package com.rrhh.trabajadores.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "trabajadores_trabajador")
public class Trabajador {
    @Id
    @Column(length = 36, columnDefinition = "CHAR(36)")
    private String id;
    @Column(name = "tenant_id", nullable = false, length = 36, columnDefinition = "CHAR(36)")
    private String tenantId;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String apellido;
    @Column(name = "rut_trabajador", nullable = false, length = 20)
    private String rutTrabajador;
    @Column(nullable = false)
    private String email;
    private String telefono;
    @Column(name = "departamento_id", length = 36, columnDefinition = "CHAR(36)")
    private String departamentoId;
    @Column(name = "cargo_id", length = 36, columnDefinition = "CHAR(36)")
    private String cargoId;
    @Column(name = "jefatura_id", length = 36, columnDefinition = "CHAR(36)")
    private String jefaturaId;
    @Column(name = "dias_vacaciones_disponibles", precision = 6, scale = 2)
    private BigDecimal diasVacacionesDisponibles = BigDecimal.valueOf(15);
    @Column(name = "fecha_desvinculacion")
    private Instant fechaDesvinculacion;
    @Column(nullable = false)
    private boolean activo = true;
    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;
}
