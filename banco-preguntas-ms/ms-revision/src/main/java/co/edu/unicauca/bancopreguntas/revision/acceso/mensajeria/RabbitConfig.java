package co.edu.unicauca.bancopreguntas.revision.acceso.mensajeria;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia RabbitMQ de ms-revision.
 * Consume:  pregunta.enviada-revision
 * Publica:  revision.revisores-asignados y revision.pregunta-evaluada
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "banco.eventos";

    public static final String RK_PREGUNTA_ENVIADA = "pregunta.enviada-revision";
    public static final String RK_REVISORES_ASIGNADOS = "revision.revisores-asignados";
    public static final String RK_PREGUNTA_EVALUADA = "revision.pregunta-evaluada";

    public static final String COLA_PREGUNTAS_ENVIADAS = "ms-revision.preguntas-enviadas";

    @Bean
    public TopicExchange exchangeEventos() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue colaPreguntasEnviadas() {
        return QueueBuilder.durable(COLA_PREGUNTAS_ENVIADAS).build();
    }

    @Bean
    public Binding bindingPreguntasEnviadas(Queue colaPreguntasEnviadas, TopicExchange exchangeEventos) {
        return BindingBuilder.bind(colaPreguntasEnviadas).to(exchangeEventos).with(RK_PREGUNTA_ENVIADA);
    }

    @Bean
    public MessageConverter conversorJson(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter conversor = new Jackson2JsonMessageConverter(objectMapper);
        conversor.setAlwaysConvertToInferredType(true);
        return conversor;
    }
}
