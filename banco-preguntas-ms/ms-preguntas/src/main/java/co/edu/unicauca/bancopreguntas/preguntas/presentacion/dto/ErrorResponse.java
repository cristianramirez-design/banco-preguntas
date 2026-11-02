package co.edu.unicauca.bancopreguntas.preguntas.presentacion.dto;

import java.util.List;

/** Respuesta de error: mensaje para el usuario, detalle y campos a resaltar en rojo. */
public record ErrorResponse(String mensaje, List<String> errores, List<String> campos) {

    public static ErrorResponse simple(String mensaje) {
        return new ErrorResponse(mensaje, List.of(), List.of());
    }
}
