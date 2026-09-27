package co.edu.unicauca.bancopreguntas.domain.validacion.reglas;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.CampoPregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.ErrorValidacion;
import co.edu.unicauca.bancopreguntas.domain.validacion.IReglaValidacion;

import java.util.Optional;

/**
 * Diseno Centrado en Evidencia: la pregunta directa debe formularse como
 * una interrogacion explicita, no como un enunciado incompleto.
 */
public class ReglaEnunciadoInterrogativo implements IReglaValidacion {

    private static final int LONGITUD_MINIMA = 10;

    @Override
    public Optional<ErrorValidacion> validar(Pregunta pregunta) {
        String enunciado = pregunta.getEnunciado();
        if (enunciado == null || enunciado.isBlank()) {
            return Optional.of(new ErrorValidacion(CampoPregunta.ENUNCIADO,
                    "La pregunta directa es obligatoria."));
        }
        if (enunciado.trim().length() < LONGITUD_MINIMA) {
            return Optional.of(new ErrorValidacion(CampoPregunta.ENUNCIADO,
                    "La pregunta directa debe tener al menos " + LONGITUD_MINIMA + " caracteres."));
        }
        if (!enunciado.trim().endsWith("?")) {
            return Optional.of(new ErrorValidacion(CampoPregunta.ENUNCIADO,
                    "La pregunta directa debe terminar con el signo '?'."));
        }
        return Optional.empty();
    }
}
