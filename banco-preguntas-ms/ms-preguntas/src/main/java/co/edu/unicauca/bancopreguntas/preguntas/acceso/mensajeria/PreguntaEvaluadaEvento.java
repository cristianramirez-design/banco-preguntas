package co.edu.unicauca.bancopreguntas.preguntas.acceso.mensajeria;

import java.time.LocalDateTime;

/**
 * Evento que publica ms-revision cuando termina la revision de una pregunta (HU-5).
 * ms-preguntas solo necesita el identificador y el resultado; ignora el resto de campos.
 */
public record PreguntaEvaluadaEvento(
        String preguntaId,
        String codigo,
        String resultado,
        LocalDateTime fecha) {

    public boolean aprobada() {
        return "APROBADA".equalsIgnoreCase(resultado);
    }
}
