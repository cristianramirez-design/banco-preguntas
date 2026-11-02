package co.edu.unicauca.bancopreguntas.revision.domain.puerto;

import co.edu.unicauca.bancopreguntas.revision.domain.evento.PreguntaEvaluadaEvento;
import co.edu.unicauca.bancopreguntas.revision.domain.evento.RevisoresAsignadosEvento;

/** Puerto de salida hacia el bus de eventos (RabbitMQ). */
public interface IPublicadorEventos {

    void revisoresAsignados(RevisoresAsignadosEvento evento);

    void preguntaEvaluada(PreguntaEvaluadaEvento evento);
}
