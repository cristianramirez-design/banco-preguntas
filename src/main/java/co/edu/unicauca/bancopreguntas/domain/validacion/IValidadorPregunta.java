package co.edu.unicauca.bancopreguntas.domain.validacion;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

/** Abstraccion del validador (principio de Inversion de Dependencias). */
public interface IValidadorPregunta {

    ResultadoValidacion validar(Pregunta pregunta);
}
