package com.rrhh.trabajadores.repository;

import com.rrhh.trabajadores.model.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CargoRepository extends JpaRepository<Cargo, String> {
    List<Cargo> findByTenantId(String tenantId);
    Optional<Cargo> findByIdAndTenantId(String id, String tenantId);
}
