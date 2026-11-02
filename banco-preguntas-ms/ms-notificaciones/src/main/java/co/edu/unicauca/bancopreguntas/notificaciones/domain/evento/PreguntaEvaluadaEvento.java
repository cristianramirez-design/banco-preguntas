package co.edu.unicauca.bancopreguntas.notificaciones.domain.evento;

import java.time.LocalDateTime;
import java.util.List;

/** Copia local del evento que publica ms-revision al terminar la HU-5. */
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

    public boolean aprobada() {
        return "APROBADA".equalsIgnoreCase(resultado);
    }
}
