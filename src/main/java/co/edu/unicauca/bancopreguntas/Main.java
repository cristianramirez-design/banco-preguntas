package co.edu.unicauca.bancopreguntas;

import co.edu.unicauca.bancopreguntas.acceso.email.ConfiguracionCorreo;
import co.edu.unicauca.bancopreguntas.acceso.email.GatewayCorreoExterno;
import co.edu.unicauca.bancopreguntas.acceso.email.ServicioEmailAsincrono;
import co.edu.unicauca.bancopreguntas.acceso.email.ServicioEmailJakartaMail;
import co.edu.unicauca.bancopreguntas.acceso.email.ServicioEmailSmtpAdapter;
import co.edu.unicauca.bancopreguntas.acceso.fabrica.RepositoryFactory;
import co.edu.unicauca.bancopreguntas.domain.notificacion.IServicioEmail;
import co.edu.unicauca.bancopreguntas.domain.notificacion.NotificadorBitacora;
import co.edu.unicauca.bancopreguntas.domain.notificacion.NotificadorEmail;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IPreguntaRepository;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IUsuarioRepository;
import co.edu.unicauca.bancopreguntas.domain.servicio.AsignacionService;
import co.edu.unicauca.bancopreguntas.domain.servicio.IAsignacionService;
import co.edu.unicauca.bancopreguntas.domain.servicio.IPreguntaService;
import co.edu.unicauca.bancopreguntas.domain.servicio.PreguntaService;
import co.edu.unicauca.bancopreguntas.domain.validacion.IValidadorPregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.ValidadorFactory;
import co.edu.unicauca.bancopreguntas.presentacion.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.nio.file.Path;

/**
 * Punto de entrada. Aqui se hace el cableado de dependencias (composition root):
 * es el unico lugar del sistema que conoce las clases concretas de cada capa.
 *
 * Persistencia: SQLite por defecto (archivo banco-preguntas.db). Para usar memoria:
 *   java -Dpersistencia=MEMORIA -jar banco-preguntas.jar
 * Correo: real por SMTP si existe correo.properties; si no, simulado en consola.
 */
public class Main {

    public static void main(String[] args) {
        RepositoryFactory fabrica = RepositoryFactory.obtener(tipoPersistencia());
        IPreguntaRepository preguntaRepository = fabrica.crearPreguntaRepository();
        IUsuarioRepository usuarioRepository = fabrica.crearUsuarioRepository();

        IValidadorPregunta validador = ValidadorFactory.porDefecto();
        IPreguntaService preguntaService =
                new PreguntaService(preguntaRepository, validador, fabrica.crearGeneradorCodigo());

        IAsignacionService asignacionService =
                new AsignacionService(preguntaRepository, usuarioRepository);
        asignacionService.registrarObservador(new NotificadorEmail(crearServicioEmail()));
        asignacionService.registrarObservador(new NotificadorBitacora());

        if (preguntaRepository.listarTodas().isEmpty()) {
            DatosDemo.cargar(preguntaService);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) {
                // Se conserva el look and feel por defecto.
            }
            new VentanaPrincipal(preguntaService, asignacionService, usuarioRepository)
                    .setVisible(true);
        });
    }

    private static RepositoryFactory.Tipo tipoPersistencia() {
        String valor = System.getProperty("persistencia", "SQLITE").trim().toUpperCase();
        return RepositoryFactory.Tipo.valueOf(valor);
    }

    private static IServicioEmail crearServicioEmail() {
        return ConfiguracionCorreo.desdeArchivo(Path.of(ConfiguracionCorreo.ARCHIVO_POR_DEFECTO))
                .<IServicioEmail>map(config -> {
                    System.out.println("Correo real habilitado via " + config.getHost());
                    return new ServicioEmailAsincrono(new ServicioEmailJakartaMail(config));
                })
                .orElseGet(() -> {
                    System.out.println("Sin correo.properties: el correo se simula en consola.");
                    return new ServicioEmailSmtpAdapter(new GatewayCorreoExterno());
                });
    }
}
