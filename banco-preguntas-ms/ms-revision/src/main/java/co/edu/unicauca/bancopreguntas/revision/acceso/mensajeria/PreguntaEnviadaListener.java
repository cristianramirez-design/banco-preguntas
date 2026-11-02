package co.edu.unicauca.bancopreguntas.revision.acceso.mensajeria;

import co.edu.unicauca.bancopreguntas.revision.domain.evento.PreguntaEnviadaARevisionEvento;
import co.edu.unicauca.bancopreguntas.revision.domain.servicio.AsignacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Patron Observer (observador): cada pregunta enviada a revision entra a la bandeja del administrador. */
@Component
public class PreguntaEnviadaListener {

    private static final Logger LOG = LoggerFactory.getLogger(PreguntaEnviadaListener.class);

    private final AsignacionService asignacion;

    public PreguntaEnviadaListener(AsignacionService asignacion) {
        this.asignacion = asignacion;
    }

    @RabbitListener(queues = RabbitConfig.COLA_PREGUNTAS_ENVIADAS)
    public void alRecibir(PreguntaEnviadaARevisionEvento evento) {
        try {
            asignacion.registrarPendiente(evento);
            LOG.info("Pregunta {} recibida para asignar revisores", evento.codigo());
        } catch (RuntimeException e) {
            LOG.error("No se pudo registrar la pregunta enviada {}: {}", evento.preguntaId(), e.getMessage());
        }
    }
}
