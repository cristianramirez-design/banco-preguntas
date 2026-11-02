package co.edu.unicauca.bancopreguntas.revision.presentacion.dto;

import java.util.List;

public record ErrorResponse(String mensaje, List<String> errores) {

    public static ErrorResponse de(String mensaje) {
        return new ErrorResponse(mensaje, List.of(mensaje));
    }
}
