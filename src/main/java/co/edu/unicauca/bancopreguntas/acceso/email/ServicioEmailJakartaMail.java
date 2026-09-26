package co.edu.unicauca.bancopreguntas.acceso.email;

import co.edu.unicauca.bancopreguntas.domain.notificacion.IServicioEmail;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

/**
 * Patron GoF Adapter: adapta la API de Jakarta Mail (Session, MimeMessage, Transport)
 * a la interfaz simple que necesita el dominio. Envia correos reales por SMTP (HU-4, CA1).
 */
public class ServicioEmailJakartaMail implements IServicioEmail {

    private final Session sesion;
    private final String remitente;

    public ServicioEmailJakartaMail(ConfiguracionCorreo config) {
        Properties props = new Properties();
        props.put("mail.smtp.host", config.getHost());
        props.put("mail.smtp.port", String.valueOf(config.getPuerto()));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(config.isStarttls()));
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        this.sesion = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getUsuario(), config.getContrasena());
            }
        });
        this.remitente = config.getRemitente();
    }

    @Override
    public void enviar(String destinatario, String asunto, String cuerpo) {
        try {
            MimeMessage mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto, "UTF-8");
            mensaje.setText(cuerpo, "UTF-8");
            Transport.send(mensaje);
            System.out.println("Correo enviado a " + destinatario);
        } catch (MessagingException e) {
            throw new IllegalStateException("No se pudo enviar el correo a " + destinatario, e);
        }
    }
}
