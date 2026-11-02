package co.edu.unicauca.bancopreguntas.notificaciones.acceso.mensajeria;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.evento.PreguntaEvaluadaEvento;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.evento.RevisoresAsignadosEvento;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.servicio.NotificacionFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Patron Observer: reacciona a los eventos de ms-revision enviando los correos por medio de la fachada. */
@Component
public class EventosRevisionListener {

    private static final Logger LOG = LoggerFactory.getLogger(EventosRevisionListener.class);

    private final NotificacionFacade notificaciones;

    public EventosRevisionListener(NotificacionFacade notificaciones) {
        this.notificaciones = notificaciones;
    }

    @RabbitListener(queues = RabbitConfig.COLA_REVISORES_ASIGNADOS)
    public void alAsignarRevisores(RevisoresAsignadosEvento evento) {
        try {
            int n = notificaciones.notificarRevisoresAsignados(evento).size();
            LOG.info("Pregunta {}: {} correo(s) de asignacion procesados", evento.codigo(), n);
        } catch (RuntimeException e) {
            LOG.error("Error notificando asignacion de {}: {}", evento.preguntaId(), e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitConfig.COLA_PREGUNTA_EVALUADA)
    public void alEvaluarPregunta(PreguntaEvaluadaEvento evento) {
        try {
            int n = notificaciones.notificarPreguntaEvaluada(evento).size();
            LOG.info("Pregunta {} {}: {} correo(s) procesados", evento.codigo(), evento.resultado(), n);
        } catch (RuntimeException e) {
            LOG.error("Error notificando evaluacion de {}: {}", evento.preguntaId(), e.getMessage());
        }
    }
}
