package co.edu.unicauca.bancopreguntas.revision.presentacion.dto;

import co.edu.unicauca.bancopreguntas.revision.domain.model.Decision;
import jakarta.validation.constraints.NotNull;

public record EvaluacionRequest(@NotNull(message = "Debe elegir APROBAR o RECHAZAR") Decision decision,
                                String observaciones) {
}
