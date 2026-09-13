package com.rrhh.trabajadores.controller;

import com.rrhh.trabajadores.dto.ApiResponse;
import com.rrhh.trabajadores.dto.request.ActualizarTrabajadorRequest;
import com.rrhh.trabajadores.dto.request.CrearTrabajadorRequest;
import com.rrhh.trabajadores.dto.response.TrabajadorResponse;
import com.rrhh.trabajadores.model.Cargo;
import com.rrhh.trabajadores.model.Departamento;
import com.rrhh.trabajadores.service.TrabajadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TrabajadorController {
    private final TrabajadorService trabajadorService;

    public TrabajadorController(TrabajadorService trabajadorService) {
        this.trabajadorService = trabajadorService;
    }

    @GetMapping("/trabajadores")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN_RRHH','JEFATURA')")
    public ApiResponse<List<TrabajadorResponse>> listar() {
        return ApiResponse.ok(trabajadorService.listar(), "Trabajadores del tenant");
    }

    @GetMapping("/trabajadores/{trabajador_id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN_RRHH','JEFATURA','TRABAJADOR')")
    public ApiResponse<TrabajadorResponse> obtener(@PathVariable("trabajador_id") String trabajadorId) {
        return ApiResponse.ok(trabajadorService.obtener(trabajadorId), "Ficha de trabajador");
    }

    @PostMapping("/trabajadores")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN_RRHH')")
    public ResponseEntity<ApiResponse<TrabajadorResponse>> crear(@Valid @RequestBody CrearTrabajadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(trabajadorService.crear(request), "Trabajador creado"));
    }

    @PatchMapping("/trabajadores/{trabajador_id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN_RRHH')")
    public ApiResponse<TrabajadorResponse> actualizar(
            @PathVariable("trabajador_id") String trabajadorId,
            @RequestBody ActualizarTrabajadorRequest request
    ) {
        return ApiResponse.ok(trabajadorService.actualizar(trabajadorId, request), "Trabajador actualizado");
    }

    @DeleteMapping("/trabajadores/{trabajador_id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN_RRHH')")
    public ApiResponse<Map<String, Boolean>> eliminar(@PathVariable("trabajador_id") String trabajadorId) {
        trabajadorService.softDelete(trabajadorId);
        return ApiResponse.ok(Map.of("activo", false), "Trabajador desvinculado (soft delete)");
    }

    @GetMapping("/departamentos")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<Departamento>> departamentos() {
        return ApiResponse.ok(trabajadorService.departamentos(), "Departamentos del tenant");
    }

    @GetMapping("/cargos")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<Cargo>> cargos() {
        return ApiResponse.ok(trabajadorService.cargos(), "Cargos del tenant");
    }
}
