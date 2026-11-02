package co.edu.unicauca.bancopreguntas.preguntas.acceso.mensajeria;

import co.edu.unicauca.bancopreguntas.preguntas.domain.evento.PreguntaEnviadaARevisionEvento;
import co.edu.unicauca.bancopreguntas.preguntas.domain.servicio.IPublicadorEventos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Implementacion del puerto IPublicadorEventos sobre RabbitMQ. */
@Component
public class PublicadorEventosRabbit implements IPublicadorEventos {

    private static final Logger LOG = LoggerFactory.getLogger(PublicadorEventosRabbit.class);

    private final RabbitTemplate rabbitTemplate;

    public PublicadorEventosRabbit(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void preguntaEnviadaARevision(PreguntaEnviadaARevisionEvento evento) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.RK_PREGUNTA_ENVIADA, evento);
        LOG.info("Evento publicado {} -> pregunta {}", RabbitConfig.RK_PREGUNTA_ENVIADA, evento.codigo());
    }
}
