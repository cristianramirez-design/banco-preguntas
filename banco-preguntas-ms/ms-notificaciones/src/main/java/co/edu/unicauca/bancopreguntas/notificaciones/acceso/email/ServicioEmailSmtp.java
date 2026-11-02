package co.edu.unicauca.bancopreguntas.notificaciones.acceso.email;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.IServicioEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Patron Adapter: adapta JavaMailSender de Spring (SMTP, p. ej. Mailtrap) al puerto IServicioEmail.
 *
 * El plan gratuito de Mailtrap rechaza varios correos enviados en el mismo segundo
 * ("Too many emails per second"). Por eso este adaptador deja un intervalo minimo entre
 * envios y, si el servidor aun asi rechaza el correo, reintenta con esperas crecientes.
 */
public class ServicioEmailSmtp implements IServicioEmail {

    private static final Logger LOG = LoggerFactory.getLogger(ServicioEmailSmtp.class);

    static final long INTERVALO_MINIMO_MS = 2_000;
    static final long[] ESPERAS_REINTENTO_MS = {3_000, 6_000, 10_000};

    private final JavaMailSender mailSender;
    private final String remitente;
    private long ultimoEnvio;

    public ServicioEmailSmtp(JavaMailSender mailSender, String remitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    @Override
    public synchronized void enviar(String para, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(para);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);

        for (int intento = 0; ; intento++) {
            esperarIntervalo();
            try {
                mailSender.send(mensaje);
                ultimoEnvio = System.currentTimeMillis();
                return;
            } catch (MailException e) {
                ultimoEnvio = System.currentTimeMillis();
                if (intento >= ESPERAS_REINTENTO_MS.length) {
                    throw e;
                }
                long espera = ESPERAS_REINTENTO_MS[intento];
                LOG.warn("El servidor SMTP rechazo el correo a {} ({}). Reintento en {} s",
                        para, e.getMessage(), espera / 1000);
                dormir(espera);
            }
        }
    }

    private void esperarIntervalo() {
        long transcurrido = System.currentTimeMillis() - ultimoEnvio;
        if (transcurrido < INTERVALO_MINIMO_MS) {
            dormir(INTERVALO_MINIMO_MS - transcurrido);
        }
    }

    private static void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Envio de correo interrumpido", e);
        }
    }
}
