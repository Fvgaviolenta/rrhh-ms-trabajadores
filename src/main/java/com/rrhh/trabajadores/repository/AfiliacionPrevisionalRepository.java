package com.rrhh.trabajadores.repository;

import com.rrhh.trabajadores.model.AfiliacionPrevisional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AfiliacionPrevisionalRepository extends JpaRepository<AfiliacionPrevisional, String> {
    List<AfiliacionPrevisional> findByTrabajadorId(String trabajadorId);
}
