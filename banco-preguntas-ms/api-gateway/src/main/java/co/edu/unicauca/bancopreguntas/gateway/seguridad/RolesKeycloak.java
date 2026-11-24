package co.edu.unicauca.bancopreguntas.gateway.seguridad;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Lee los roles del token de Keycloak. Keycloak los pone en el claim realm_access.roles
 * (por ejemplo ["AUTOR", "REVISOR"]) y Spring Security los necesita como ROLE_AUTOR, ROLE_REVISOR.
 */
public final class RolesKeycloak {

    public static final List<String> ROLES_DEL_SISTEMA = List.of("AUTOR", "REVISOR", "ADMINISTRADOR");

    private RolesKeycloak() {
    }

    /** Roles del sistema presentes en el token (ignora roles internos de Keycloak como offline_access). */
    public static List<String> roles(Jwt jwt) {
        Object realmAccess = jwt.getClaims().get("realm_access");
        if (!(realmAccess instanceof Map<?, ?> mapa)) {
            return List.of();
        }
        Object roles = mapa.get("roles");
        if (!(roles instanceof Collection<?> lista)) {
            return List.of();
        }
        return lista.stream().map(String::valueOf).map(String::toUpperCase)
                .filter(ROLES_DEL_SISTEMA::contains).distinct().sorted().toList();
    }

    public static Collection<GrantedAuthority> autoridades(Jwt jwt) {
        return roles(jwt).stream().<GrantedAuthority>map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
    }

    /** Login del usuario: el claim preferred_username de Keycloak (o el subject si no viene). */
    public static String login(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return (username == null || username.isBlank() ? jwt.getSubject() : username).trim().toLowerCase();
    }
}
