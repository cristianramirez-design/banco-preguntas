package co.edu.unicauca.bancopreguntas.preguntas.domain.busqueda;

import co.edu.unicauca.bancopreguntas.preguntas.domain.model.Pregunta;

public class FiltroPorSubtema implements IFiltroPregunta {

    private final String subtema;

    public FiltroPorSubtema(String subtema) {
        this.subtema = subtema == null ? "" : subtema.trim().toLowerCase();
    }

    @Override
    public boolean cumple(Pregunta pregunta) {
        return pregunta.getSubtema().toLowerCase().contains(subtema);
    }
}
