package co.edu.unicauca.bancopreguntas.notificaciones.acceso.mensajeria;

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

/** ms-notificaciones solo consume: revision.revisores-asignados y revision.pregunta-evaluada. */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "banco.eventos";
    public static final String RK_REVISORES_ASIGNADOS = "revision.revisores-asignados";
    public static final String RK_PREGUNTA_EVALUADA = "revision.pregunta-evaluada";
    public static final String COLA_REVISORES_ASIGNADOS = "ms-notificaciones.revisores-asignados";
    public static final String COLA_PREGUNTA_EVALUADA = "ms-notificaciones.pregunta-evaluada";

    @Bean
    public TopicExchange exchangeEventos() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue colaRevisoresAsignados() {
        return QueueBuilder.durable(COLA_REVISORES_ASIGNADOS).build();
    }

    @Bean
    public Queue colaPreguntaEvaluada() {
        return QueueBuilder.durable(COLA_PREGUNTA_EVALUADA).build();
    }

    @Bean
    public Binding bindingRevisoresAsignados(Queue colaRevisoresAsignados, TopicExchange exchangeEventos) {
        return BindingBuilder.bind(colaRevisoresAsignados).to(exchangeEventos).with(RK_REVISORES_ASIGNADOS);
    }

    @Bean
    public Binding bindingPreguntaEvaluada(Queue colaPreguntaEvaluada, TopicExchange exchangeEventos) {
        return BindingBuilder.bind(colaPreguntaEvaluada).to(exchangeEventos).with(RK_PREGUNTA_EVALUADA);
    }

    @Bean
    public MessageConverter conversorJson(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter conversor = new Jackson2JsonMessageConverter(objectMapper);
        conversor.setAlwaysConvertToInferredType(true);
        return conversor;
    }
}
