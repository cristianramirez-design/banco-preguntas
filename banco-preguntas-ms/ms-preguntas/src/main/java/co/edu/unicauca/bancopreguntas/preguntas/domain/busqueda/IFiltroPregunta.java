package co.edu.unicauca.bancopreguntas.preguntas.domain.busqueda;

import co.edu.unicauca.bancopreguntas.preguntas.domain.model.Pregunta;

/** Patron GoF Strategy aplicado al filtrado del listado (HU03 del listado). */
public interface IFiltroPregunta {

    boolean cumple(Pregunta pregunta);
}
