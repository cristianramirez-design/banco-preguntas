package co.edu.unicauca.bancopreguntas.gateway.seguridad;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Propaga la identidad a los microservicios. Primero BORRA cualquier X-Usuario o X-Roles que mande
 * el cliente (asi nadie puede hacerse pasar por otro) y luego los vuelve a poner con los datos del
 * token JWT ya validado. El encabezado Authorization tambien se reenvia sin cambios.
 */
@Component
public class FiltroIdentidad implements GlobalFilter, Ordered {

    public static final String USUARIO = "X-Usuario";
    public static final String ROLES = "X-Roles";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .filter(JwtAuthenticationToken.class::isInstance)
                .cast(JwtAuthenticationToken.class)
                .map(token -> conIdentidad(exchange, token.getToken()))
                .defaultIfEmpty(sinIdentidad(exchange))
                .flatMap(chain::filter);
    }

    private ServerWebExchange conIdentidad(ServerWebExchange exchange, Jwt jwt) {
        return conEncabezados(exchange, RolesKeycloak.login(jwt), String.join(",", RolesKeycloak.roles(jwt)));
    }

    private ServerWebExchange sinIdentidad(ServerWebExchange exchange) {
        return conEncabezados(exchange, null, null);
    }

    /** Envuelve la peticion con una copia de sus encabezados ya corregidos (los originales son de solo lectura). */
    private ServerWebExchange conEncabezados(ServerWebExchange exchange, String login, String roles) {
        HttpHeaders encabezados = new HttpHeaders();
        encabezados.putAll(exchange.getRequest().getHeaders());
        encabezados.remove(USUARIO);
        encabezados.remove(ROLES);
        if (login != null) {
            encabezados.set(USUARIO, login);
            encabezados.set(ROLES, roles);
        }
        ServerHttpRequest peticion = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                return encabezados;
            }
        };
        return exchange.mutate().request(peticion).build();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
