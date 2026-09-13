package com.rrhh.trabajadores.repository;

import com.rrhh.trabajadores.model.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrabajadorRepository extends JpaRepository<Trabajador, String> {
    List<Trabajador> findByTenantIdAndActivoTrue(String tenantId);
    Optional<Trabajador> findByIdAndTenantId(String id, String tenantId);
    boolean existsByTenantIdAndRutTrabajador(String tenantId, String rut);
}
