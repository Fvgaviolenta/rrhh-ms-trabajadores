package com.rrhh.trabajadores.repository;

import com.rrhh.trabajadores.model.AfiliacionSalud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AfiliacionSaludRepository extends JpaRepository<AfiliacionSalud, String> {
    List<AfiliacionSalud> findByTrabajadorId(String trabajadorId);
}
