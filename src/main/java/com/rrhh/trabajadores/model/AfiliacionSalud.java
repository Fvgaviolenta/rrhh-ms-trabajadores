package com.rrhh.trabajadores.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "trabajadores_afiliacion_salud")
public class AfiliacionSalud {
    @Id
    @Column(length = 36, columnDefinition = "CHAR(36)")
    private String id;
    @Column(name = "trabajador_id", nullable = false, length = 36, columnDefinition = "CHAR(36)")
    private String trabajadorId;
    @Column(name = "isapre_id", length = 36, columnDefinition = "CHAR(36)")
    private String isapreId;
    @Column(name = "tipo_salud", nullable = false, length = 40)
    private String tipoSalud;
    @Column(name = "fecha_vigencia", nullable = false)
    private LocalDate fechaVigencia;
    @Column(nullable = false, length = 30)
    private String estado;
}
