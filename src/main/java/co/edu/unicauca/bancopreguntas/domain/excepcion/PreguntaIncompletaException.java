package co.edu.unicauca.bancopreguntas.domain.excepcion;

import co.edu.unicauca.bancopreguntas.domain.validacion.CampoPregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.ResultadoValidacion;

import java.util.List;
import java.util.Set;

/**
 * HU-2, CA2: se intenta enviar a revision una pregunta que no paso la
 * validacion estructural.
 */
public class PreguntaIncompletaException extends ReglaNegocioException {

    public static final String MENSAJE_USUARIO =
            "La pregunta debe estar completa antes de enviarla a revision";

    private final List<String> mensajes;
    private final Set<CampoPregunta> campos;

    public PreguntaIncompletaException(ResultadoValidacion resultado) {
        super(MENSAJE_USUARIO);
        this.mensajes = resultado.getMensajes();
        this.campos = resultado.getCamposInvalidos();
    }

    public List<String> getErrores() {
        return mensajes;
    }

    public Set<CampoPregunta> getCamposInvalidos() {
        return campos;
    }
}
