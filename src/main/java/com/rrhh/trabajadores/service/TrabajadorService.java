package com.rrhh.trabajadores.service;

import com.rrhh.trabajadores.dto.request.ActualizarTrabajadorRequest;
import com.rrhh.trabajadores.dto.request.CrearTrabajadorRequest;
import com.rrhh.trabajadores.dto.response.TrabajadorResponse;
import com.rrhh.trabajadores.exception.DomainException;
import com.rrhh.trabajadores.model.Cargo;
import com.rrhh.trabajadores.model.Departamento;
import com.rrhh.trabajadores.model.Trabajador;
import com.rrhh.trabajadores.repository.CargoRepository;
import com.rrhh.trabajadores.repository.DepartamentoRepository;
import com.rrhh.trabajadores.repository.SuperadminTrabajadorRepository;
import com.rrhh.trabajadores.repository.TrabajadorRepository;
import com.rrhh.trabajadores.security.Roles;
import com.rrhh.trabajadores.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class TrabajadorService {
    private final TrabajadorRepository trabajadorRepository;
    private final DepartamentoRepository departamentoRepository;
    private final CargoRepository cargoRepository;
    private final SuperadminTrabajadorRepository superadminTrabajadorRepository;
    private final TenantContext tenantContext;

    public TrabajadorService(
            TrabajadorRepository trabajadorRepository,
            DepartamentoRepository departamentoRepository,
            CargoRepository cargoRepository,
            SuperadminTrabajadorRepository superadminTrabajadorRepository,
            TenantContext tenantContext
    ) {
        this.trabajadorRepository = trabajadorRepository;
        this.departamentoRepository = departamentoRepository;
        this.cargoRepository = cargoRepository;
        this.superadminTrabajadorRepository = superadminTrabajadorRepository;
        this.tenantContext = tenantContext;
    }

    public List<TrabajadorResponse> listar() {
        TenantContext.AuthenticatedUser actor = tenantContext.require();
        String authority = Roles.authorityFromClaim(actor.role());
        if (Roles.TRABAJADOR.equals(authority)) {
            throw new DomainException(403, "Un trabajador no puede listar fichas de otros");
        }
        List<Trabajador> trabajadores = trabajadorRepository.findByTenantIdOrderByApellidoAscNombreAsc(actor.tenantId());
        if (Roles.ADMIN_RRHH.equals(authority)) {
            Set<String> superadminIds = superadminTrabajadorRepository.encontrarIdsPorTenant(actor.tenantId());
            trabajadores = trabajadores.stream().filter(trabajador -> !superadminIds.contains(trabajador.getId())).toList();
        }
        return trabajadores.stream().map(this::toResponse).toList();
    }

    public List<TrabajadorResponse> listarPorTenantPlataforma(String tenantId) {
        exigirOperadorSaas();
        return trabajadorRepository.findByTenantIdOrderByApellidoAscNombreAsc(tenantId).stream().map(this::toResponse).toList();
    }

    public TrabajadorResponse obtener(String trabajadorId) {
        TenantContext.AuthenticatedUser actor = tenantContext.require();
        Trabajador trabajador = buscar(trabajadorId, actor.tenantId());
        String authority = Roles.authorityFromClaim(actor.role());
        validarQueAdminNoGestioneSuperadmin(actor, trabajadorId);
        if (Roles.TRABAJADOR.equals(authority) && (actor.trabajadorId() == null || !actor.trabajadorId().equals(trabajadorId))) {
            throw new DomainException(403, "No puede ver la ficha de otro trabajador");
        }
        return toResponse(trabajador);
    }

    @Transactional
    public TrabajadorResponse crear(CrearTrabajadorRequest request) {
        String tenantId = tenantContext.require().tenantId();
        if (trabajadorRepository.existsByTenantIdAndRutTrabajador(tenantId, request.rutTrabajador())) {
            throw new DomainException(400, "RUT duplicado en el tenant", "rut_trabajador");
        }
        String departamentoId = resolverDepartamentoId(tenantId, request.departamentoId());
        String cargoId = resolverCargoId(tenantId, request.cargoId());
        validarOrganigrama(tenantId, departamentoId, cargoId, request.jefaturaId());
        Trabajador trabajador = new Trabajador();
        trabajador.setId(UUID.randomUUID().toString());
        trabajador.setTenantId(tenantId);
        trabajador.setNombre(request.nombre());
        trabajador.setApellido(request.apellido());
        trabajador.setRutTrabajador(request.rutTrabajador());
        trabajador.setEmail(request.email().toLowerCase());
        trabajador.setTelefono(request.telefono());
        trabajador.setDepartamentoId(departamentoId);
        trabajador.setCargoId(cargoId);
        trabajador.setJefaturaId(request.jefaturaId());
        trabajador.setDiasVacacionesDisponibles(request.diasVacacionesDisponibles() == null ? BigDecimal.valueOf(15) : request.diasVacacionesDisponibles());
        trabajador.setActivo(request.activo() == null || request.activo());
        trabajador.setCreadoEn(Instant.now());
        return toResponse(trabajadorRepository.save(trabajador));
    }

    @Transactional
    public TrabajadorResponse actualizar(String trabajadorId, ActualizarTrabajadorRequest request) {
        TenantContext.AuthenticatedUser actor = tenantContext.require();
        String tenantId = actor.tenantId();
        Trabajador trabajador = buscar(trabajadorId, tenantId);
        validarQueAdminNoGestioneSuperadmin(actor, trabajadorId);
        String departamentoId = request.departamentoId() == null
                ? null
                : resolverDepartamentoId(tenantId, request.departamentoId());
        String cargoId = request.cargoId() == null
                ? null
                : resolverCargoId(tenantId, request.cargoId());
        validarOrganigrama(tenantId, departamentoId, cargoId, request.jefaturaId());
        if (request.nombre() != null) trabajador.setNombre(request.nombre());
        if (request.apellido() != null) trabajador.setApellido(request.apellido());
        if (request.email() != null) trabajador.setEmail(request.email().toLowerCase());
        if (request.telefono() != null) trabajador.setTelefono(request.telefono());
        if (request.departamentoId() != null) trabajador.setDepartamentoId(departamentoId);
        if (request.cargoId() != null) trabajador.setCargoId(cargoId);
        if (request.jefaturaId() != null) trabajador.setJefaturaId(request.jefaturaId());
        if (request.diasVacacionesDisponibles() != null) trabajador.setDiasVacacionesDisponibles(request.diasVacacionesDisponibles());
        if (request.activo() != null) {
            trabajador.setActivo(request.activo());
            trabajador.setFechaDesvinculacion(Boolean.TRUE.equals(request.activo()) ? null : Instant.now());
        }
        return toResponse(trabajadorRepository.save(trabajador));
    }

    @Transactional
    public void softDelete(String trabajadorId) {
        TenantContext.AuthenticatedUser actor = tenantContext.require();
        String tenantId = actor.tenantId();
        Trabajador trabajador = buscar(trabajadorId, tenantId);
        validarQueAdminNoGestioneSuperadmin(actor, trabajadorId);
        trabajador.setActivo(false);
        trabajador.setFechaDesvinculacion(Instant.now());
        trabajadorRepository.save(trabajador);
    }

    public List<Departamento> departamentos() {
        return departamentoRepository.findByTenantId(tenantContext.require().tenantId());
    }

    public List<Cargo> cargos() {
        return cargoRepository.findByTenantId(tenantContext.require().tenantId());
    }

    private Trabajador buscar(String id, String tenantId) {
        return trabajadorRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new DomainException(404, "Trabajador no encontrado"));
    }

    private void exigirOperadorSaas() {
        if (!Roles.OPERADOR_SAAS.equals(Roles.authorityFromClaim(tenantContext.current().role()))) {
            throw new DomainException(403, "Acceso exclusivo para el operador SaaS");
        }
    }

    private void validarQueAdminNoGestioneSuperadmin(TenantContext.AuthenticatedUser actor, String trabajadorId) {
        if (Roles.ADMIN_RRHH.equals(Roles.authorityFromClaim(actor.role()))
                && superadminTrabajadorRepository.esSuperadmin(actor.tenantId(), trabajadorId)) {
            // No revelar la existencia ni los datos de una cuenta de control.
            throw new DomainException(404, "Trabajador no encontrado");
        }
    }

    private void validarOrganigrama(String tenantId, String departamentoId, String cargoId, String jefaturaId) {
        if (departamentoId != null && departamentoRepository.findByIdAndTenantId(departamentoId, tenantId).isEmpty()) {
            throw new DomainException(400, "Departamento inválido para el tenant", "departamento_id");
        }
        if (cargoId != null && cargoRepository.findByIdAndTenantId(cargoId, tenantId).isEmpty()) {
            throw new DomainException(400, "Cargo inválido para el tenant", "cargo_id");
        }
        if (jefaturaId != null && trabajadorRepository.findByIdAndTenantId(jefaturaId, tenantId).isEmpty()) {
            throw new DomainException(400, "Jefatura inválida para el tenant", "jefatura_id");
        }
    }

    private String resolverDepartamentoId(String tenantId, String valor) {
        if (valor == null || valor.isBlank()) return null;
        return departamentoRepository.findByIdAndTenantId(valor, tenantId)
                .map(Departamento::getId)
                .orElseGet(() -> departamentoRepository
                        .findByTenantIdAndNombreDepartamentoIgnoreCase(tenantId, valor.trim())
                        .map(Departamento::getId)
                        .orElseGet(() -> {
                            Departamento departamento = new Departamento();
                            departamento.setId(UUID.randomUUID().toString());
                            departamento.setTenantId(tenantId);
                            departamento.setNombreDepartamento(valor.trim());
                            return departamentoRepository.save(departamento).getId();
                        }));
    }

    private String resolverCargoId(String tenantId, String valor) {
        if (valor == null || valor.isBlank()) return null;
        return cargoRepository.findByIdAndTenantId(valor, tenantId)
                .map(Cargo::getId)
                .orElseGet(() -> cargoRepository
                        .findByTenantIdAndNombreCargoIgnoreCase(tenantId, valor.trim())
                        .map(Cargo::getId)
                        .orElseGet(() -> {
                            Cargo cargo = new Cargo();
                            cargo.setId(UUID.randomUUID().toString());
                            cargo.setTenantId(tenantId);
                            cargo.setNombreCargo(valor.trim());
                            return cargoRepository.save(cargo).getId();
                        }));
    }

    private TrabajadorResponse toResponse(Trabajador t) {
        return new TrabajadorResponse(
                t.getId(), t.getTenantId(), t.getNombre(), t.getApellido(), t.getRutTrabajador(),
                t.getEmail(), t.getTelefono(), t.getDepartamentoId(), t.getCargoId(), t.getJefaturaId(),
                t.getDiasVacacionesDisponibles(), t.isActivo()
        );
    }
}
