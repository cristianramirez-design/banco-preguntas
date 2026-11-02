package co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.model.Notificacion;

import java.util.List;

public interface INotificacionRepositorio {

    Notificacion guardar(Notificacion notificacion);

    /** Mas recientes primero. Si destinatario es null devuelve todas. */
    List<Notificacion> listar(String destinatarioLogin);
}
