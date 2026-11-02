package co.edu.unicauca.bancopreguntas.preguntas.presentacion.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PreguntaResponse(
        String id,
        String codigo,
        String contexto,
        String enunciado,
        List<OpcionResponse> opciones,
        String justificacion,
        String bibliografia,
        String competencia,
        String tema,
        String subtema,
        String nivelDificultad,
        String autorLogin,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaEnvioRevision,
        EstadoResponse estado,
        List<String> revisores) {

    public record OpcionResponse(String texto, boolean correcta) {
    }

    public record EstadoResponse(String nombre, String color, boolean editable) {
    }
}
