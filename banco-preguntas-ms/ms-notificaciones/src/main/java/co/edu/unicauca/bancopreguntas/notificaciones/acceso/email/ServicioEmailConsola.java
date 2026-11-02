package co.edu.unicauca.bancopreguntas.notificaciones.acceso.email;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.IServicioEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Se usa cuando no hay SMTP configurado: el correo se muestra en la consola del microservicio. */
public class ServicioEmailConsola implements IServicioEmail {

    private static final Logger LOG = LoggerFactory.getLogger(ServicioEmailConsola.class);

    @Override
    public void enviar(String para, String asunto, String cuerpo) {
        LOG.info("\n===== CORREO (modo consola) =====\nPara: {}\nAsunto: {}\n{}\n=================================",
                para, asunto, cuerpo);
    }
}
