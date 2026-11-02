package co.edu.unicauca.bancopreguntas.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto unico de entrada del frontend. Enruta cada prefijo /api/... al microservicio duenio
 * y publica un Swagger UI que agrupa la documentacion de todos los servicios.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
