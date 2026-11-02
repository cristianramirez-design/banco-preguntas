package co.edu.unicauca.bancopreguntas.preguntas.presentacion.dto;

import java.util.List;

public record PaginaResponse<T>(
        List<T> contenido,
        int pagina,
        int tamanio,
        int totalElementos,
        int totalPaginas) {
}
