package co.edu.unicauca.bancopreguntas.acceso.email;

import co.edu.unicauca.bancopreguntas.domain.notificacion.IServicioEmail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Patron GoF Decorator: envuelve cualquier IServicioEmail y hace el envio en un
 * hilo aparte, para que la conexion SMTP no congele la interfaz Swing.
 * Si el envio falla, el error se registra y la asignacion no se revierte.
 */
public class ServicioEmailAsincrono implements IServicioEmail {

    private final IServicioEmail decorado;
    private final ExecutorService ejecutor;

    public ServicioEmailAsincrono(IServicioEmail decorado) {
        this(decorado, Executors.newSingleThreadExecutor(r -> {
            Thread hilo = new Thread(r, "envio-correo");
            hilo.setDaemon(true);
            return hilo;
        }));
    }

    public ServicioEmailAsincrono(IServicioEmail decorado, ExecutorService ejecutor) {
        this.decorado = decorado;
        this.ejecutor = ejecutor;
    }

    @Override
    public void enviar(String destinatario, String asunto, String cuerpo) {
        ejecutor.submit(() -> {
            try {
                decorado.enviar(destinatario, asunto, cuerpo);
            } catch (RuntimeException e) {
                System.err.println("Fallo el envio de correo: " + e.getMessage());
            }
        });
    }
}
