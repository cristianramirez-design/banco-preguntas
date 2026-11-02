package co.edu.unicauca.bancopreguntas.notificaciones.domain.evento;

import java.time.LocalDateTime;
import java.util.List;

/** Copia local del evento que publica ms-revision (cada microservicio es duenio de sus clases). */
public record RevisoresAsignadosEvento(
        String preguntaId,
        String codigo,
        String enunciado,
        String tema,
        String competencia,
        String autorLogin,
        List<String> revisores,
        String administradorLogin,
        LocalDateTime fecha) {
}
