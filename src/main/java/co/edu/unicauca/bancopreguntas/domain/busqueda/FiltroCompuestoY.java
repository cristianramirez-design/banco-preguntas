package co.edu.unicauca.bancopreguntas.domain.busqueda;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

import java.util.ArrayList;
import java.util.List;

/** Patron GoF Composite: combina varios filtros con la conjuncion logica Y. */
public class FiltroCompuestoY implements IFiltroPregunta {

    private final List<IFiltroPregunta> filtros = new ArrayList<>();

    public FiltroCompuestoY agregar(IFiltroPregunta filtro) {
        if (filtro != null) {
            filtros.add(filtro);
        }
        return this;
    }

    @Override
    public boolean cumple(Pregunta pregunta) {
        for (IFiltroPregunta filtro : filtros) {
            if (!filtro.cumple(pregunta)) {
                return false;
            }
        }
        return true;
    }
}
