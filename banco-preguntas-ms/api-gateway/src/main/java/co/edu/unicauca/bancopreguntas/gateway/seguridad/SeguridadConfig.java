package co.edu.unicauca.bancopreguntas.gateway.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * El API Gateway es el punto de control de seguridad (patron Gateway Offloading):
 *  1. Exige un token JWT valido, firmado por Keycloak (OAuth2 Resource Server).
 *  2. Autoriza cada ruta segun el rol que trae el token (AUTOR, REVISOR, ADMINISTRADOR).
 *  3. FiltroIdentidad reenvia a los microservicios el usuario autenticado en X-Usuario.
 * Los microservicios no se exponen fuera de la red de Docker: solo se llega a ellos por aqui.
 */
@Configuration
@EnableWebFluxSecurity
public class SeguridadConfig {

    @Bean
    public SecurityWebFilterChain cadenaSeguridad(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)   // API sin estado con tokens: no usa cookies
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(rutas -> rutas
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        // Documentacion (Swagger) publica; para probar se usa el boton Authorize con el token
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/webjars/**", "/v3/api-docs/**",
                                "/docs/**", "/actuator/health").permitAll()
                        // ms-usuarios
                        .pathMatchers(HttpMethod.POST, "/api/usuarios").hasRole("ADMINISTRADOR")
                        // ms-preguntas: solo el autor crea, edita y envia a revision (HU-1, HU-2, HU-3, HU-6)
                        .pathMatchers(HttpMethod.POST, "/api/preguntas").hasRole("AUTOR")
                        .pathMatchers(HttpMethod.PUT, "/api/preguntas/**").hasRole("AUTOR")
                        .pathMatchers(HttpMethod.PATCH, "/api/preguntas/**").hasRole("AUTOR")
                        // ms-revision: el administrador asigna (HU-4) y el revisor evalua (HU-5)
                        .pathMatchers("/api/revisiones/pendientes", "/api/revisiones/asignaciones")
                        .hasRole("ADMINISTRADOR")
                        .pathMatchers("/api/revisiones/mias", "/api/revisiones/*/evaluacion").hasRole("REVISOR")
                        // ms-notificaciones: historial de correos para cualquier rol autenticado
                        .pathMatchers(HttpMethod.GET, "/api/notificaciones").hasAnyRole("AUTOR", "REVISOR", "ADMINISTRADOR")
                        // Todo lo demas exige al menos estar autenticado
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(conversorJwt())))
                .build();
    }

    /** Convierte el token de Keycloak en un usuario de Spring con sus roles (ROLE_AUTOR, ...). */
    private ReactiveJwtAuthenticationConverterAdapter conversorJwt() {
        JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
        conversor.setJwtGrantedAuthoritiesConverter(RolesKeycloak::autoridades);
        conversor.setPrincipalClaimName("preferred_username");
        return new ReactiveJwtAuthenticationConverterAdapter(conversor);
    }
}
