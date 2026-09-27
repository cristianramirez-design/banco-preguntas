package co.edu.unicauca.bancopreguntas.domain.validacion;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

import java.util.Optional;

/**
 * Patron GoF Strategy. Cada regla estructural de la HU-1/HU-3 es una estrategia
 * independiente; agregar una regla nueva no obliga a modificar el validador
 * (principio Open/Closed).
 */
public interface IReglaValidacion {

    /** Devuelve el error, o vacio si la pregunta cumple la regla. */
    Optional<ErrorValidacion> validar(Pregunta pregunta);
}
