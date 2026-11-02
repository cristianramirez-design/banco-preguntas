package co.edu.unicauca.bancopreguntas.revision.domain.politica;

import co.edu.unicauca.bancopreguntas.revision.domain.model.ResultadoRevision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Revision;

import java.util.List;
import java.util.Optional;

/**
 * Patron GoF Strategy: regla para decidir el resultado de la pregunta a partir de las
 * revisiones de una ronda. Devuelve vacio mientras falten revisores por evaluar.
 */
public interface IPoliticaDecision {

    Optional<ResultadoRevision> decidir(List<Revision> revisionesDeLaRonda);

    String getNombre();
}
