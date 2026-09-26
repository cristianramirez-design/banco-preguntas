package co.edu.unicauca.bancopreguntas.domain.notificacion;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

/** Evento de dominio publicado cuando el administrador asigna revisores (HU04). */
public class EventoAsignacion {

    private final Pregunta pregunta;
    private final List<Usuario> revisores;
    private final String administradorLogin;
    private final LocalDateTime momento;

    public EventoAsignacion(Pregunta pregunta, List<Usuario> revisores, String administradorLogin) {
        this.pregunta = pregunta;
        this.revisores = List.copyOf(revisores);
        this.administradorLogin = administradorLogin;
        this.momento = LocalDateTime.now();
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public List<Usuario> getRevisores() {
        return revisores;
    }

    public String getAdministradorLogin() {
        return administradorLogin;
    }

    public LocalDateTime getMomento() {
        return momento;
    }
}
