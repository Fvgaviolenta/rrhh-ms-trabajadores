package com.rrhh.trabajadores.controller;

import com.rrhh.trabajadores.TrabajadoresApplication;
import com.rrhh.trabajadores.config.TestJwtConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TrabajadoresApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJwtConfig.class)
// Los tokens los resuelve TestJwtConfig: "admin", "trabajador" y "otro-tenant".
class TrabajadorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminListaTrabajadoresDelTenant() throws Exception {
        mockMvc.perform(get("/api/v1/trabajadores").header("Authorization", "Bearer admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));
    }

    @Test
    void otroTenantNoVeFicha() throws Exception {
        mockMvc.perform(get("/api/v1/trabajadores/ffffffff-ffff-ffff-ffff-ffffffffffff").header("Authorization", "Bearer otro-tenant"))
                .andExpect(status().isNotFound());
    }

    @Test
    void trabajadorNoListaTodos() throws Exception {
        mockMvc.perform(get("/api/v1/trabajadores").header("Authorization", "Bearer trabajador"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional // revierte el alta para no cambiar el conteo de adminListaTrabajadoresDelTenant
    void creaTrabajadorConRutUnico() throws Exception {
        mockMvc.perform(post("/api/v1/trabajadores")
                        .header("Authorization", "Bearer admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre":"Luis",
                                  "apellido":"Gomez",
                                  "rut_trabajador":"11.111.111-1",
                                  "email":"luis.gomez@rrhh.local",
                                  "departamento_id":"dddddddd-dddd-dddd-dddd-dddddddd0001",
                                  "cargo_id":"eeeeeeee-eeee-eeee-eeee-eeeeeeee0001"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.datos.tenant_id").value("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
    }
}
