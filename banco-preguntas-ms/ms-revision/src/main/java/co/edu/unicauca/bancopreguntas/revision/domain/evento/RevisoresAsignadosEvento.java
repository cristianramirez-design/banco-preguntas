package co.edu.unicauca.bancopreguntas.revision.domain.evento;

import java.time.LocalDateTime;
import java.util.List;

/** HU-4: el administrador asigno revisores. Lo consumen ms-preguntas y ms-notificaciones. */
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
