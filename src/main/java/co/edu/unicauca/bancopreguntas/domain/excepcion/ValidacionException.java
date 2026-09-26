package co.edu.unicauca.bancopreguntas.domain.excepcion;

import co.edu.unicauca.bancopreguntas.domain.validacion.CampoPregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.ResultadoValidacion;

import java.util.List;
import java.util.Set;

/** Se lanza cuando la validacion estructural de una pregunta falla (HU-1, CA2). */
public class ValidacionException extends RuntimeException {

    /** Mensaje exacto acordado en los criterios de aceptacion. */
    public static final String MENSAJE_USUARIO = "No se pudo guardar la pregunta";

    private final List<String> mensajes;
    private final Set<CampoPregunta> campos;

    public ValidacionException(ResultadoValidacion resultado) {
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
