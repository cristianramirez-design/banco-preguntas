package co.edu.unicauca.bancopreguntas.revision.presentacion.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

/** El tamano de la lista (1 a 3) lo valida el dominio para devolver el mensaje exacto de la HU-4. */
public record AsignacionRequest(@NotBlank String preguntaId, List<String> revisores, @NotBlank String administrador) {
}
