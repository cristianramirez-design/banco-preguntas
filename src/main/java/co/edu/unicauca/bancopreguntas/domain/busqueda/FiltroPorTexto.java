package co.edu.unicauca.bancopreguntas.domain.busqueda;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

/** Busqueda libre por ID o contenido, como en el prototipo movil de la HU-3. */
public class FiltroPorTexto implements IFiltroPregunta {

    private final String texto;

    public FiltroPorTexto(String texto) {
        this.texto = texto == null ? "" : texto.trim().toLowerCase();
    }

    @Override
    public boolean cumple(Pregunta pregunta) {
        if (texto.isEmpty()) {
            return true;
        }
        return contiene(pregunta.getCodigo())
                || contiene(pregunta.getEnunciado())
                || contiene(pregunta.getContexto())
                || contiene(pregunta.getTema())
                || contiene(pregunta.getSubtema())
                || contiene(pregunta.getCompetencia());
    }

    private boolean contiene(String valor) {
        return valor != null && valor.toLowerCase().contains(texto);
    }
}
