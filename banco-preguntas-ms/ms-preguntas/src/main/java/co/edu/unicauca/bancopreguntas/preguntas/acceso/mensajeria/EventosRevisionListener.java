package co.edu.unicauca.bancopreguntas.preguntas.acceso.mensajeria;

import co.edu.unicauca.bancopreguntas.preguntas.domain.servicio.IPreguntaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Patron Observer entre microservicios: ms-preguntas se suscribe a los eventos de ms-revision
 * y actualiza el ciclo de vida de la pregunta (patron State en el dominio).
 */
@Component
public class EventosRevisionListener {

    private static final Logger LOG = LoggerFactory.getLogger(EventosRevisionListener.class);

    private final IPreguntaService servicio;

    public EventosRevisionListener(IPreguntaService servicio) {
        this.servicio = servicio;
    }

    @RabbitListener(queues = RabbitConfig.COLA_REVISORES_ASIGNADOS)
    public void alAsignarRevisores(RevisoresAsignadosEvento evento) {
        try {
            servicio.marcarEnRevision(evento.preguntaId(), evento.revisores());
            LOG.info("Pregunta {} -> En revision ({})", evento.codigo(), evento.revisores());
        } catch (RuntimeException e) {
            LOG.error("No se pudo procesar RevisoresAsignados de {}: {}", evento.codigo(), e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitConfig.COLA_PREGUNTA_EVALUADA)
    public void alEvaluarPregunta(PreguntaEvaluadaEvento evento) {
        try {
            servicio.registrarResultadoRevision(evento.preguntaId(), evento.aprobada());
            LOG.info("Pregunta {} -> {}", evento.codigo(), evento.resultado());
        } catch (RuntimeException e) {
            LOG.error("No se pudo procesar PreguntaEvaluada de {}: {}", evento.codigo(), e.getMessage());
        }
    }
}
