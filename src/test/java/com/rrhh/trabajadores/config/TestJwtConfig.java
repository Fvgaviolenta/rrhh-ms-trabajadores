package com.rrhh.trabajadores.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.Map;

/**
 * Decoder de prueba: el valor del token elige los claims. Los tests mandan {@code Authorization: Bearer <token>}
 * para que la request pase por {@code JwtTenantConverter}, que asigna el rol y el tenant. El post-processor
 * {@code jwt()} de spring-security-test se salta ese converter y deja solo authorities SCOPE_*.
 */
@TestConfiguration
public class TestJwtConfig {
    public static final String TENANT_DEMO = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
    public static final String TENANT_OTRO = "cccccccc-cccc-cccc-cccc-cccccccccccc";
    /** Empresa del token "admin-suspendida"; TenantSuspendidoIT la crea en identity_tenant y la suspende. */
    public static final String TENANT_SUSPENDIBLE = "99999999-9999-9999-9999-999999999999";

    @Bean
    @Primary
    JwtDecoder jwtDecoder() {
        return token -> {
            Map<String, Object> claims = switch (token) {
                case "trabajador" -> Map.of(
                        "email", "ana.perez@rrhh.local",
                        "custom:tenant_id", TENANT_DEMO,
                        "custom:role", "Trabajador",
                        "custom:trabajador_id", "ffffffff-ffff-ffff-ffff-ffffffffffff");
                case "otro-tenant" -> Map.of(
                        "email", "admin.b@rrhh.local",
                        "custom:tenant_id", TENANT_OTRO,
                        "custom:role", "Admin de RRHH");
                case "admin-suspendida" -> Map.of(
                        "email", "admin.suspendida@rrhh.local",
                        "custom:tenant_id", TENANT_SUSPENDIBLE,
                        "custom:role", "Admin de RRHH");
                case "admin-plataforma" -> Map.of(
                        "email", "owner@gmail.com",
                        "custom:role", "AdminPlataforma");
                case "admin-plataforma-con-tenant" -> Map.of(
                        "email", "owner@gmail.com",
                        "custom:tenant_id", TENANT_SUSPENDIBLE,
                        "custom:role", "AdminPlataforma");
                default -> Map.of(
                        "email", "admin.demo@rrhh.local",
                        "custom:tenant_id", TENANT_DEMO,
                        "custom:role", "Admin de RRHH");
            };
            return Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .subject("test-" + token)
                    .claims(c -> c.putAll(claims))
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .build();
        };
    }
}
