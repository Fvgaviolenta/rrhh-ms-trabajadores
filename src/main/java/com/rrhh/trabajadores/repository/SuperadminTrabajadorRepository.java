package com.rrhh.trabajadores.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Consulta mínima a Identity para no exponer fichas vinculadas a cuentas de
 * control del SaaS a administradores del tenant.
 */
@Repository
public class SuperadminTrabajadorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Set<String> encontrarIdsPorTenant(String tenantId) {
        @SuppressWarnings("unchecked")
        List<Object> ids = entityManager.createNativeQuery("""
                SELECT trabajador_id
                FROM identity_usuario
                WHERE tenant_id = :tenantId
                  AND trabajador_id IS NOT NULL
                  AND rol IN ('SuperAdmin', 'SUPERADMIN', 'ROLE_SUPERADMIN')
                """)
                .setParameter("tenantId", tenantId)
                .getResultList();
        return ids.stream().map(Object::toString).collect(Collectors.toSet());
    }

    public boolean esSuperadmin(String tenantId, String trabajadorId) {
        return encontrarIdsPorTenant(tenantId).contains(trabajadorId);
    }
}
