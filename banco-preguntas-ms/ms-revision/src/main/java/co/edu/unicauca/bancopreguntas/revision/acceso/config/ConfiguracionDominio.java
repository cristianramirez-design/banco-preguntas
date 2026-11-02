package co.edu.unicauca.bancopreguntas.revision.acceso.config;

import co.edu.unicauca.bancopreguntas.revision.domain.politica.IPoliticaDecision;
import co.edu.unicauca.bancopreguntas.revision.domain.politica.PoliticaDecisionFactory;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IDirectorioUsuarios;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IPreguntaPendienteRepositorio;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IPublicadorEventos;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IRevisionRepositorio;
import co.edu.unicauca.bancopreguntas.revision.domain.servicio.AsignacionService;
import co.edu.unicauca.bancopreguntas.revision.domain.servicio.EvaluacionService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Composition root de ms-revision: el dominio no conoce Spring. */
@Configuration
public class ConfiguracionDominio {

    /** Factory Method: la politica de decision se elige por configuracion (revision.politica). */
    @Bean
    public IPoliticaDecision politicaDecision(@Value("${revision.politica:UNANIMIDAD}") String nombre) {
        return PoliticaDecisionFactory.crear(nombre);
    }

    @Bean
    public AsignacionService asignacionService(IPreguntaPendienteRepositorio pendientes,
                                               IRevisionRepositorio revisiones,
                                               IDirectorioUsuarios directorio,
                                               IPublicadorEventos publicador) {
        return new AsignacionService(pendientes, revisiones, directorio, publicador);
    }

    @Bean
    public EvaluacionService evaluacionService(IRevisionRepositorio revisiones,
                                               IPreguntaPendienteRepositorio pendientes,
                                               IPoliticaDecision politica,
                                               IPublicadorEventos publicador) {
        return new EvaluacionService(revisiones, pendientes, politica, publicador);
    }

    @Bean
    public OpenAPI documentacionApi() {
        return new OpenAPI()
                // URL relativa: "Try it out" funciona igual desde el gateway (8080) o desde el servicio
                .addServersItem(new Server().url("/").description("Servidor actual"))
                .info(new Info()
                .title("ms-revision - Asignacion y Evaluacion")
                .version("2.0")
                .description("Bandeja de preguntas enviadas, asignacion de 1 a 3 revisores (HU-4) "
                        + "y evaluacion por parte del revisor (HU-5)."));
    }
}
