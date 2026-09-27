package co.edu.unicauca.bancopreguntas.domain.validacion.reglas;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.CampoPregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.ErrorValidacion;
import co.edu.unicauca.bancopreguntas.domain.validacion.IReglaValidacion;

import java.util.Optional;

public class ReglaNivelDificultad implements IReglaValidacion {

    @Override
    public Optional<ErrorValidacion> validar(Pregunta pregunta) {
        if (pregunta.getNivelDificultad() == null) {
            return Optional.of(new ErrorValidacion(CampoPregunta.NIVEL_DIFICULTAD,
                    "Debe seleccionar el nivel de dificultad."));
        }
        return Optional.empty();
    }
}
