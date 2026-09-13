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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TrabajadoresApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJwtConfig.class)
class TrabajadorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminListaTrabajadoresDelTenant() throws Exception {
        mockMvc.perform(get("/api/v1/trabajadores").with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(1));
    }

    @Test
    void otroTenantNoVeFicha() throws Exception {
        mockMvc.perform(get("/api/v1/trabajadores/ffffffff-ffff-ffff-ffff-ffffffffffff").with(otroTenantJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void trabajadorNoListaTodos() throws Exception {
        mockMvc.perform(get("/api/v1/trabajadores").with(trabajadorJwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void creaTrabajadorConRutUnico() throws Exception {
        mockMvc.perform(post("/api/v1/trabajadores")
                        .with(adminJwt())
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

    private static RequestPostProcessor adminJwt() {
        return jwt().jwt(b -> b.subject("a").claim("email", "admin.demo@rrhh.local")
                .claim("custom:tenant_id", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .claim("custom:role", "Admin de RRHH"));
    }

    private static RequestPostProcessor trabajadorJwt() {
        return jwt().jwt(b -> b.subject("t").claim("email", "ana.perez@rrhh.local")
                .claim("custom:tenant_id", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .claim("custom:role", "Trabajador")
                .claim("custom:trabajador_id", "ffffffff-ffff-ffff-ffff-ffffffffffff"));
    }

    private static RequestPostProcessor otroTenantJwt() {
        return jwt().jwt(b -> b.subject("b").claim("email", "admin.b@rrhh.local")
                .claim("custom:tenant_id", "cccccccc-cccc-cccc-cccc-cccccccccccc")
                .claim("custom:role", "Admin de RRHH"));
    }
}
