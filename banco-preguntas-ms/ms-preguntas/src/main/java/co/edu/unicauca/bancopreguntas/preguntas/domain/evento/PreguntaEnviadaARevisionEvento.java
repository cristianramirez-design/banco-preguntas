package co.edu.unicauca.bancopreguntas.preguntas.domain.evento;

import java.time.LocalDateTime;

/**
 * Evento de dominio publicado cuando el autor envia su pregunta a revision (HU-2).
 * Lo consume ms-revision para mostrar la pregunta en la bandeja del administrador.
 * Lleva una copia de los datos que necesita el consumidor (no comparten base de datos).
 */
public record PreguntaEnviadaARevisionEvento(
        String preguntaId,
        String codigo,
        String enunciado,
        String tema,
        String competencia,
        String autorLogin,
        LocalDateTime fechaEnvio) {
}
