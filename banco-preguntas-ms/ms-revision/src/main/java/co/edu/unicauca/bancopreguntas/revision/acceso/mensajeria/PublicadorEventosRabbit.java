package co.edu.unicauca.bancopreguntas.revision.acceso.mensajeria;

import co.edu.unicauca.bancopreguntas.revision.domain.evento.PreguntaEvaluadaEvento;
import co.edu.unicauca.bancopreguntas.revision.domain.evento.RevisoresAsignadosEvento;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IPublicadorEventos;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Patron Observer (lado del sujeto): publica los eventos de revision en el exchange. */
@Component
public class PublicadorEventosRabbit implements IPublicadorEventos {

    private final RabbitTemplate rabbit;

    public PublicadorEventosRabbit(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @Override
    public void revisoresAsignados(RevisoresAsignadosEvento evento) {
        rabbit.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.RK_REVISORES_ASIGNADOS, evento);
    }

    @Override
    public void preguntaEvaluada(PreguntaEvaluadaEvento evento) {
        rabbit.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.RK_PREGUNTA_EVALUADA, evento);
    }
}
