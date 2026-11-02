package co.edu.unicauca.bancopreguntas.preguntas.presentacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** Datos que envia el frontend para crear o editar una pregunta (HU-1, HU-3 CA4). */
public record PreguntaRequest(
        String contexto,
        String enunciado,
        @Schema(description = "Cinco opciones: cuatro distractores y la respuesta correcta")
        List<String> opciones,
        @Schema(description = "Posicion (0-4) de la respuesta correcta en la lista de opciones")
        Integer indiceCorrecta,
        String justificacion,
        String bibliografia,
        String competencia,
        String tema,
        String subtema,
        @Schema(description = "BAJA, MEDIA o ALTA")
        String nivelDificultad) {
}
