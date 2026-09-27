package co.edu.unicauca.bancopreguntas.domain.busqueda;

import co.edu.unicauca.bancopreguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

public class FiltroPorDificultad implements IFiltroPregunta {

    private final NivelDificultad nivel;

    public FiltroPorDificultad(NivelDificultad nivel) {
        this.nivel = nivel;
    }

    @Override
    public boolean cumple(Pregunta pregunta) {
        return pregunta.getNivelDificultad() == nivel;
    }
}
