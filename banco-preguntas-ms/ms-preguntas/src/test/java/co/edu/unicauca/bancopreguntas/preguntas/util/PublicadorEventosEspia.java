package co.edu.unicauca.bancopreguntas.preguntas.util;

import co.edu.unicauca.bancopreguntas.preguntas.domain.evento.PreguntaEnviadaARevisionEvento;
import co.edu.unicauca.bancopreguntas.preguntas.domain.servicio.IPublicadorEventos;

import java.util.ArrayList;
import java.util.List;

/** Doble de prueba (espia): guarda los eventos en lugar de enviarlos a RabbitMQ. */
public class PublicadorEventosEspia implements IPublicadorEventos {

    public final List<PreguntaEnviadaARevisionEvento> publicados = new ArrayList<>();

    @Override
    public void preguntaEnviadaARevision(PreguntaEnviadaARevisionEvento evento) {
        publicados.add(evento);
    }
}
