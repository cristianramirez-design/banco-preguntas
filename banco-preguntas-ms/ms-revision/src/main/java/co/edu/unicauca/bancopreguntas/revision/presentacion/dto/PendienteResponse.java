package co.edu.unicauca.bancopreguntas.revision.presentacion.dto;

import co.edu.unicauca.bancopreguntas.revision.domain.model.PreguntaPendiente;

import java.time.LocalDateTime;

public record PendienteResponse(String preguntaId, String codigo, String enunciado, String tema,
                                String competencia, String autorLogin, LocalDateTime fechaEnvio, int ronda) {

    public static PendienteResponse de(PreguntaPendiente p) {
        return new PendienteResponse(p.getPreguntaId(), p.getCodigo(), p.getEnunciado(), p.getTema(),
                p.getCompetencia(), p.getAutorLogin(), p.getFechaEnvio(), p.getRonda());
    }
}
