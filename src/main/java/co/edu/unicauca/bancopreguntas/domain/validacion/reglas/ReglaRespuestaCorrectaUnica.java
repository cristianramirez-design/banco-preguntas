package co.edu.unicauca.bancopreguntas.domain.validacion.reglas;

import co.edu.unicauca.bancopreguntas.domain.model.Opcion;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.CampoPregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.ErrorValidacion;
import co.edu.unicauca.bancopreguntas.domain.validacion.IReglaValidacion;

import java.util.Optional;

/** Seleccion multiple con UNICA respuesta: debe haber una y solo una marcada. */
public class ReglaRespuestaCorrectaUnica implements IReglaValidacion {

    @Override
    public Optional<ErrorValidacion> validar(Pregunta pregunta) {
        long correctas = pregunta.getOpciones().stream().filter(Opcion::esCorrecta).count();

        if (correctas == 0) {
            return Optional.of(new ErrorValidacion(CampoPregunta.RESPUESTA_CORRECTA,
                    "Debe marcar cual de las opciones es la respuesta correcta."));
        }
        if (correctas > 1) {
            return Optional.of(new ErrorValidacion(CampoPregunta.RESPUESTA_CORRECTA,
                    "Solo puede marcarse una respuesta correcta."));
        }

        Opcion correcta = pregunta.getRespuestaCorrecta();
        if (correcta.estaVacia()) {
            return Optional.of(new ErrorValidacion(CampoPregunta.RESPUESTA_CORRECTA,
                    "La opcion marcada como correcta no puede estar vacia."));
        }
        return Optional.empty();
    }
}
