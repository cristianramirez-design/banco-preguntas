package co.edu.unicauca.bancopreguntas.gateway.seguridad;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Seguridad: lectura de usuario y roles desde el token de Keycloak. */
class RolesKeycloakTest {

    private Jwt token(Map<String, Object> claims) {
        Jwt.Builder b = Jwt.withTokenValue("t").header("alg", "RS256").subject("uuid-123")
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300));
        claims.forEach(b::claim);
        return b.build();
    }

    @Test
    @DisplayName("Toma solo los roles del sistema del claim realm_access.roles")
    void rolesDelSistema() {
        Jwt jwt = token(Map.of("preferred_username", "autor2",
                "realm_access", Map.of("roles", List.of("offline_access", "REVISOR", "AUTOR", "default-roles-banco"))));

        assertEquals(List.of("AUTOR", "REVISOR"), RolesKeycloak.roles(jwt));
        assertEquals(List.of("ROLE_AUTOR", "ROLE_REVISOR"),
                RolesKeycloak.autoridades(jwt).stream().map(a -> a.getAuthority()).toList());
    }

    @Test
    @DisplayName("Sin realm_access el usuario no tiene roles")
    void sinRoles() {
        assertTrue(RolesKeycloak.roles(token(Map.of("preferred_username", "x"))).isEmpty());
    }

    @Test
    @DisplayName("El login es preferred_username en minusculas; si falta, el subject")
    void login() {
        assertEquals("admin", RolesKeycloak.login(token(Map.of("preferred_username", " Admin "))));
        assertEquals("uuid-123", RolesKeycloak.login(token(Map.of("scope", "openid"))));
    }
}
