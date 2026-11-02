package co.edu.unicauca.bancopreguntas.revision.domain.evento;

import java.time.LocalDateTime;

/** Evento que publica ms-preguntas (HU-2). ms-revision lo consume. */
public record PreguntaEnviadaARevisionEvento(
        String preguntaId,
        String codigo,
        String enunciado,
        String tema,
        String competencia,
        String autorLogin,
        LocalDateTime fechaEnvio) {
}
