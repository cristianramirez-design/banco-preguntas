package co.edu.unicauca.bancopreguntas.notificaciones.acceso.email;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.IServicioEmail;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/** Patron Adapter: adapta JavaMailSender de Spring (SMTP, p. ej. Mailtrap) al puerto IServicioEmail. */
public class ServicioEmailSmtp implements IServicioEmail {

    private final JavaMailSender mailSender;
    private final String remitente;

    public ServicioEmailSmtp(JavaMailSender mailSender, String remitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    @Override
    public void enviar(String para, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(para);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mailSender.send(mensaje);
    }
}
