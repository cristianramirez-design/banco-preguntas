package co.edu.unicauca.bancopreguntas.notificaciones.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/** Registro de cada correo que el sistema intento enviar (se muestra en la pantalla de notificaciones). */
public class Notificacion {

    private final String id;
    private final String destinatarioLogin;
    private final String destinatarioEmail;
    private final String asunto;
    private final String cuerpo;
    private final String tipo;
    private final LocalDateTime fecha;
    private final EstadoEnvio estado;
    private final String detalleError;

    public Notificacion(String id, String destinatarioLogin, String destinatarioEmail, String asunto, String cuerpo,
                        String tipo, LocalDateTime fecha, EstadoEnvio estado, String detalleError) {
        this.id = id;
        this.destinatarioLogin = destinatarioLogin;
        this.destinatarioEmail = destinatarioEmail;
        this.asunto = asunto;
        this.cuerpo = cuerpo;
        this.tipo = tipo;
        this.fecha = fecha;
        this.estado = estado;
        this.detalleError = detalleError;
    }

    public static Notificacion enviada(Mensaje m, String email) {
        return new Notificacion(UUID.randomUUID().toString(), m.destinatarioLogin(), email, m.asunto(), m.cuerpo(),
                m.tipo(), LocalDateTime.now(), EstadoEnvio.ENVIADA, null);
    }

    public static Notificacion fallida(Mensaje m, String email, String error) {
        return new Notificacion(UUID.randomUUID().toString(), m.destinatarioLogin(), email, m.asunto(), m.cuerpo(),
                m.tipo(), LocalDateTime.now(), EstadoEnvio.FALLIDA, error);
    }

    public String getId() { return id; }
    public String getDestinatarioLogin() { return destinatarioLogin; }
    public String getDestinatarioEmail() { return destinatarioEmail; }
    public String getAsunto() { return asunto; }
    public String getCuerpo() { return cuerpo; }
    public String getTipo() { return tipo; }
    public LocalDateTime getFecha() { return fecha; }
    public EstadoEnvio getEstado() { return estado; }
    public String getDetalleError() { return detalleError; }
}
