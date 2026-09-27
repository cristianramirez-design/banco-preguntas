package co.edu.unicauca.bancopreguntas.domain.busqueda;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

public class FiltroPorCompetencia implements IFiltroPregunta {

    private final String competencia;

    public FiltroPorCompetencia(String competencia) {
        this.competencia = competencia == null ? "" : competencia.trim().toLowerCase();
    }

    @Override
    public boolean cumple(Pregunta pregunta) {
        return pregunta.getCompetencia().toLowerCase().contains(competencia);
    }
}
