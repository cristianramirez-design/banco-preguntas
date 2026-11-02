package co.edu.unicauca.bancopreguntas.preguntas.acceso.mensajeria;

import java.time.LocalDateTime;
import java.util.List;

/** Evento que publica ms-revision cuando el administrador asigna revisores (HU-4). */
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
