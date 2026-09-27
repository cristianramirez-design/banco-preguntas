package co.edu.unicauca.bancopreguntas.domain.busqueda;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

public class FiltroPorAutor implements IFiltroPregunta {

    private final String login;

    public FiltroPorAutor(String login) {
        this.login = login;
    }

    @Override
    public boolean cumple(Pregunta pregunta) {
        return pregunta.perteneceA(login);
    }
}
