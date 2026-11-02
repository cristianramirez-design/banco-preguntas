package co.edu.unicauca.bancopreguntas.preguntas.domain.busqueda;

import co.edu.unicauca.bancopreguntas.preguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.estado.IEstadoPregunta;

public class FiltroPorEstado implements IFiltroPregunta {

    private final IEstadoPregunta estado;

    public FiltroPorEstado(IEstadoPregunta estado) {
        this.estado = estado;
    }

    @Override
    public boolean cumple(Pregunta pregunta) {
        return pregunta.getEstado().getNombre().equals(estado.getNombre());
    }
}
