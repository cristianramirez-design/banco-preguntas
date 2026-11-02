package co.edu.unicauca.bancopreguntas.notificaciones.acceso.config;

import co.edu.unicauca.bancopreguntas.notificaciones.acceso.email.ServicioEmailConsola;
import co.edu.unicauca.bancopreguntas.notificaciones.acceso.email.ServicioEmailSmtp;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.IDirectorioUsuarios;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.INotificacionRepositorio;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.IServicioEmail;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.servicio.NotificacionFacade;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class ConfiguracionDominio {

    private static final Logger LOG = LoggerFactory.getLogger(ConfiguracionDominio.class);

    /**
     * Si existe correo.properties con spring.mail.host (Mailtrap), Spring crea un JavaMailSender y se
     * envian correos reales; si no, se usa el adaptador de consola para que el sistema funcione igual.
     */
    @Bean
    public IServicioEmail servicioEmail(ObjectProvider<JavaMailSender> mailSender,
                                        @Value("${notificaciones.remitente}") String remitente) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            LOG.warn("No hay SMTP configurado (correo.properties). Los correos se mostraran en consola.");
            return new ServicioEmailConsola();
        }
        LOG.info("Correos por SMTP con remitente {}", remitente);
        return new ServicioEmailSmtp(sender, remitente);
    }

    @Bean
    public NotificacionFacade notificacionFacade(IDirectorioUsuarios directorio, IServicioEmail email,
                                                 INotificacionRepositorio historial) {
        return new NotificacionFacade(directorio, email, historial);
    }

    @Bean
    public OpenAPI documentacionApi() {
        return new OpenAPI()
                // URL relativa: "Try it out" funciona igual desde el gateway (8080) o desde el servicio
                .addServersItem(new Server().url("/").description("Servidor actual"))
                .info(new Info()
                .title("ms-notificaciones - Correos")
                .version("2.0")
                .description("Escucha los eventos de revision y envia correos (HU-4 y HU-5)."));
    }
}
