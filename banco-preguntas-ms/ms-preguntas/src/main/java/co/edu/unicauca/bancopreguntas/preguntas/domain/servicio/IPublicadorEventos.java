package co.edu.unicauca.bancopreguntas.preguntas.domain.servicio;

import co.edu.unicauca.bancopreguntas.preguntas.domain.evento.PreguntaEnviadaARevisionEvento;

/**
 * Puerto de salida hacia el bus de eventos. El dominio no sabe que detras hay RabbitMQ
 * (principio de Inversion de Dependencias).
 */
public interface IPublicadorEventos {

    void preguntaEnviadaARevision(PreguntaEnviadaARevisionEvento evento);
}
