package co.edu.unicauca.bancopreguntas.revision.presentacion.dto;

import co.edu.unicauca.bancopreguntas.revision.domain.model.Revision;

import java.time.LocalDateTime;

public record RevisionResponse(String id, String preguntaId, String codigoPregunta, String enunciado, int ronda,
                               String revisorLogin, LocalDateTime fechaAsignacion, String estado,
                               String observaciones, LocalDateTime fechaEvaluacion) {

    public static RevisionResponse de(Revision r) {
        return new RevisionResponse(r.getId(), r.getPreguntaId(), r.getCodigoPregunta(), r.getEnunciado(),
                r.getRonda(), r.getRevisorLogin(), r.getFechaAsignacion(), r.getEstado().name(),
                r.getObservaciones(), r.getFechaEvaluacion());
    }
}
