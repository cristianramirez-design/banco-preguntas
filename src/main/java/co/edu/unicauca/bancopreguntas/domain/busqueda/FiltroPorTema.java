package co.edu.unicauca.bancopreguntas.domain.busqueda;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

public class FiltroPorTema implements IFiltroPregunta {

    private final String tema;

    public FiltroPorTema(String tema) {
        this.tema = tema == null ? "" : tema.trim().toLowerCase();
    }

    @Override
    public boolean cumple(Pregunta pregunta) {
        return pregunta.getTema().toLowerCase().contains(tema);
    }
}
