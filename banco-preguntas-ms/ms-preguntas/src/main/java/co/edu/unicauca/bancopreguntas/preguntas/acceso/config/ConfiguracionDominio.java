package co.edu.unicauca.bancopreguntas.preguntas.acceso.config;

import co.edu.unicauca.bancopreguntas.preguntas.domain.repositorio.IPreguntaRepository;
import co.edu.unicauca.bancopreguntas.preguntas.domain.servicio.IGeneradorCodigo;
import co.edu.unicauca.bancopreguntas.preguntas.domain.servicio.IPreguntaService;
import co.edu.unicauca.bancopreguntas.preguntas.domain.servicio.IPublicadorEventos;
import co.edu.unicauca.bancopreguntas.preguntas.domain.servicio.PreguntaService;
import co.edu.unicauca.bancopreguntas.preguntas.domain.validacion.IValidadorPregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.validacion.ValidadorFactory;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root: unico lugar donde se conectan las implementaciones concretas con el
 * dominio. El dominio no tiene anotaciones de Spring.
 */
@Configuration
public class ConfiguracionDominio {

    @Bean
    public IValidadorPregunta validadorPregunta() {
        return ValidadorFactory.porDefecto();
    }

    @Bean
    public IPreguntaService preguntaService(IPreguntaRepository repositorio,
                                            IValidadorPregunta validador,
                                            IGeneradorCodigo generadorCodigo,
                                            IPublicadorEventos publicador) {
        return new PreguntaService(repositorio, validador, generadorCodigo, publicador);
    }

    @Bean
    public OpenAPI documentacionApi() {
        return new OpenAPI()
                // URL relativa: "Try it out" funciona igual desde el gateway (8080) o desde el servicio
                .addServersItem(new Server().url("/").description("Servidor actual"))
                .info(new Info()
                .title("ms-preguntas - Gestion de Preguntas")
                .version("2.0")
                .description("Crear, editar, listar y enviar a revision preguntas Saber Pro (HU-1, HU-2, HU-3)."));
    }
}
