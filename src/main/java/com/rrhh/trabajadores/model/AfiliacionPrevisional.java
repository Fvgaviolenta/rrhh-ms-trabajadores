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
@Table(name = "trabajadores_afiliacion_previsional")
public class AfiliacionPrevisional {
    @Id
    @Column(length = 36, columnDefinition = "CHAR(36)")
    private String id;
    @Column(name = "trabajador_id", nullable = false, length = 36, columnDefinition = "CHAR(36)")
    private String trabajadorId;
    @Column(name = "afp_id", nullable = false, length = 36, columnDefinition = "CHAR(36)")
    private String afpId;
    @Column(name = "fecha_vigencia", nullable = false)
    private LocalDate fechaVigencia;
    @Column(nullable = false, length = 30)
    private String estado;
}
