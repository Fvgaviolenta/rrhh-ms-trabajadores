package com.rrhh.trabajadores.repository;

import com.rrhh.trabajadores.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartamentoRepository extends JpaRepository<Departamento, String> {
    List<Departamento> findByTenantId(String tenantId);
    Optional<Departamento> findByIdAndTenantId(String id, String tenantId);
}
