package co.edu.unicauca.bancopreguntas.notificaciones.domain;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.model.EstadoEnvio;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.model.Notificacion;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.IDirectorioUsuarios;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.INotificacionRepositorio;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.IServicioEmail;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.UsuarioInfo;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.servicio.NotificacionFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas del patron Facade con dobles en memoria. */
class NotificacionFacadeTest {

    private final List<String> enviadosA = new ArrayList<>();
    private final List<Notificacion> guardadas = new ArrayList<>();
    private boolean smtpCaido;
    private NotificacionFacade fachada;

    @BeforeEach
    void inicializar() {
        IDirectorioUsuarios directorio = login -> login.equals("fantasma") ? Optional.empty()
                : Optional.of(new UsuarioInfo(login, "Nombre", login + "@unicauca.edu.co", Set.of("REVISOR")));
        IServicioEmail email = (para, asunto, cuerpo) -> {
            if (smtpCaido) {
                throw new IllegalStateException("SMTP no disponible");
            }
            enviadosA.add(para);
        };
        INotificacionRepositorio repo = new INotificacionRepositorio() {
            public Notificacion guardar(Notificacion n) {
                guardadas.add(n);
                return n;
            }

            public List<Notificacion> listar(String d) {
                return guardadas.stream().filter(n -> d == null || d.equals(n.getDestinatarioLogin())).toList();
            }
        };
        fachada = new NotificacionFacade(directorio, email, repo);
    }

    @Test
    @DisplayName("HU-4: se envia correo a cada revisor y al autor, y queda en el historial")
    void asignacion() {
        List<Notificacion> r = fachada.notificarRevisoresAsignados(PlantillasTest.asignacion(List.of("revisor1", "revisor2")));

        assertEquals(3, r.size());
        assertEquals(List.of("revisor1@unicauca.edu.co", "revisor2@unicauca.edu.co", "autor1@unicauca.edu.co"), enviadosA);
        assertTrue(r.stream().allMatch(n -> n.getEstado() == EstadoEnvio.ENVIADA));
        assertEquals(1, fachada.historial("revisor1").size());
    }

    @Test
    @DisplayName("Un destinatario sin correo queda FALLIDA pero no impide enviar a los demas")
    void destinatarioSinCorreo() {
        List<Notificacion> r = fachada.notificarRevisoresAsignados(PlantillasTest.asignacion(List.of("fantasma", "revisor1")));

        assertEquals(EstadoEnvio.FALLIDA, r.get(0).getEstado());
        assertEquals(EstadoEnvio.ENVIADA, r.get(1).getEstado());
        assertEquals(2, enviadosA.size());
    }

    @Test
    @DisplayName("Si el SMTP falla se registra el error y no se lanza excepcion")
    void smtpCaido() {
        smtpCaido = true;
        List<Notificacion> r = fachada.notificarPreguntaEvaluada(PlantillasTest.evaluacion("APROBADA"));

        assertEquals(4, r.size());
        assertTrue(r.stream().allMatch(n -> n.getEstado() == EstadoEnvio.FALLIDA));
        assertEquals("SMTP no disponible", r.get(0).getDetalleError());
    }
}
