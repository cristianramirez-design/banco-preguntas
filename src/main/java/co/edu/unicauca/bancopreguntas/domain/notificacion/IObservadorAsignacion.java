package co.edu.unicauca.bancopreguntas.domain.notificacion;

/**
 * Patron GoF Observer. El servicio de asignacion publica el evento y no sabe
 * quien reacciona: hoy un notificador por correo y una bitacora, manana
 * podria sumarse una notificacion push sin tocar el servicio.
 */
public interface IObservadorAsignacion {

    void alAsignarRevisores(EventoAsignacion evento);
}
