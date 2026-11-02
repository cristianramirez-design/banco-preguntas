package co.edu.unicauca.bancopreguntas.revision.domain.evento;

import java.time.LocalDateTime;
import java.util.List;

/**
 * HU-5: termino la revision de la pregunta. Lo consumen ms-preguntas (cambia el estado)
 * y ms-notificaciones (avisa al autor, a los revisores y al administrador).
 */
public record PreguntaEvaluadaEvento(
        String preguntaId,
        String codigo,
        String enunciado,
        String autorLogin,
        String administradorLogin,
        String resultado,
        List<Observacion> observaciones,
        LocalDateTime fecha) {

    public record Observacion(String revisorLogin, String decision, String texto) {
    }
}
