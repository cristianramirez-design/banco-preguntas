package co.edu.unicauca.bancopreguntas.notificaciones.presentacion;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.model.Notificacion;

import java.time.LocalDateTime;

public record NotificacionResponse(String id, String destinatarioLogin, String destinatarioEmail, String asunto,
                                   String cuerpo, String tipo, LocalDateTime fecha, String estado,
                                   String detalleError) {

    public static NotificacionResponse de(Notificacion n) {
        return new NotificacionResponse(n.getId(), n.getDestinatarioLogin(), n.getDestinatarioEmail(), n.getAsunto(),
                n.getCuerpo(), n.getTipo(), n.getFecha(), n.getEstado().name(), n.getDetalleError());
    }
}
