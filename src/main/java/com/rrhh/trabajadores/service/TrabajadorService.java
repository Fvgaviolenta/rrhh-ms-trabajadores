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
import com.rrhh.trabajadores.repository.TrabajadorRepository;
import com.rrhh.trabajadores.security.Roles;
import com.rrhh.trabajadores.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TrabajadorService {
    private final TrabajadorRepository trabajadorRepository;
    private final DepartamentoRepository departamentoRepository;
    private final CargoRepository cargoRepository;
    private final TenantContext tenantContext;

    public TrabajadorService(
            TrabajadorRepository trabajadorRepository,
            DepartamentoRepository departamentoRepository,
            CargoRepository cargoRepository,
            TenantContext tenantContext
    ) {
        this.trabajadorRepository = trabajadorRepository;
        this.departamentoRepository = departamentoRepository;
        this.cargoRepository = cargoRepository;
        this.tenantContext = tenantContext;
    }

    public List<TrabajadorResponse> listar() {
        TenantContext.AuthenticatedUser actor = tenantContext.require();
        String authority = Roles.authorityFromClaim(actor.role());
        if (Roles.TRABAJADOR.equals(authority)) {
            throw new DomainException(403, "Un trabajador no puede listar fichas de otros");
        }
        return trabajadorRepository.findByTenantIdAndActivoTrue(actor.tenantId()).stream().map(this::toResponse).toList();
    }

    public TrabajadorResponse obtener(String trabajadorId) {
        TenantContext.AuthenticatedUser actor = tenantContext.require();
        Trabajador trabajador = buscar(trabajadorId, actor.tenantId());
        String authority = Roles.authorityFromClaim(actor.role());
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
        validarOrganigrama(tenantId, request.departamentoId(), request.cargoId(), request.jefaturaId());
        Trabajador trabajador = new Trabajador();
        trabajador.setId(UUID.randomUUID().toString());
        trabajador.setTenantId(tenantId);
        trabajador.setNombre(request.nombre());
        trabajador.setApellido(request.apellido());
        trabajador.setRutTrabajador(request.rutTrabajador());
        trabajador.setEmail(request.email().toLowerCase());
        trabajador.setTelefono(request.telefono());
        trabajador.setDepartamentoId(request.departamentoId());
        trabajador.setCargoId(request.cargoId());
        trabajador.setJefaturaId(request.jefaturaId());
        trabajador.setDiasVacacionesDisponibles(request.diasVacacionesDisponibles() == null ? BigDecimal.valueOf(15) : request.diasVacacionesDisponibles());
        trabajador.setActivo(true);
        trabajador.setCreadoEn(Instant.now());
        return toResponse(trabajadorRepository.save(trabajador));
    }

    @Transactional
    public TrabajadorResponse actualizar(String trabajadorId, ActualizarTrabajadorRequest request) {
        String tenantId = tenantContext.require().tenantId();
        Trabajador trabajador = buscar(trabajadorId, tenantId);
        validarOrganigrama(tenantId, request.departamentoId(), request.cargoId(), request.jefaturaId());
        if (request.nombre() != null) trabajador.setNombre(request.nombre());
        if (request.apellido() != null) trabajador.setApellido(request.apellido());
        if (request.email() != null) trabajador.setEmail(request.email().toLowerCase());
        if (request.telefono() != null) trabajador.setTelefono(request.telefono());
        if (request.departamentoId() != null) trabajador.setDepartamentoId(request.departamentoId());
        if (request.cargoId() != null) trabajador.setCargoId(request.cargoId());
        if (request.jefaturaId() != null) trabajador.setJefaturaId(request.jefaturaId());
        if (request.diasVacacionesDisponibles() != null) trabajador.setDiasVacacionesDisponibles(request.diasVacacionesDisponibles());
        return toResponse(trabajadorRepository.save(trabajador));
    }

    @Transactional
    public void softDelete(String trabajadorId) {
        String tenantId = tenantContext.require().tenantId();
        Trabajador trabajador = buscar(trabajadorId, tenantId);
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

    private TrabajadorResponse toResponse(Trabajador t) {
        return new TrabajadorResponse(
                t.getId(), t.getTenantId(), t.getNombre(), t.getApellido(), t.getRutTrabajador(),
                t.getEmail(), t.getTelefono(), t.getDepartamentoId(), t.getCargoId(), t.getJefaturaId(),
                t.getDiasVacacionesDisponibles(), t.isActivo()
        );
    }
}
