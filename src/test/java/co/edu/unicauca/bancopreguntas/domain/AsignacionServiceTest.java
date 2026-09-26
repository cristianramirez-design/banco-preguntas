package co.edu.unicauca.bancopreguntas.domain;

import co.edu.unicauca.bancopreguntas.acceso.memoria.GeneradorCodigoSecuencial;
import co.edu.unicauca.bancopreguntas.acceso.memoria.PreguntaRepositoryMemoria;
import co.edu.unicauca.bancopreguntas.acceso.memoria.UsuarioRepositoryMemoria;
import co.edu.unicauca.bancopreguntas.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.domain.notificacion.EventoAsignacion;
import co.edu.unicauca.bancopreguntas.domain.notificacion.IObservadorAsignacion;
import co.edu.unicauca.bancopreguntas.domain.notificacion.IServicioEmail;
import co.edu.unicauca.bancopreguntas.domain.notificacion.NotificadorEmail;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IPreguntaRepository;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IUsuarioRepository;
import co.edu.unicauca.bancopreguntas.domain.servicio.AsignacionService;
import co.edu.unicauca.bancopreguntas.domain.servicio.IAsignacionService;
import co.edu.unicauca.bancopreguntas.domain.servicio.IPreguntaService;
import co.edu.unicauca.bancopreguntas.domain.servicio.PreguntaService;
import co.edu.unicauca.bancopreguntas.domain.validacion.ValidadorFactory;
import co.edu.unicauca.bancopreguntas.util.PreguntaMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsignacionServiceTest {

    /** Doble de prueba (stub) del servicio de correo: evita dependencias externas. */
    private static class EmailEspia implements IServicioEmail {
        private final List<String> destinatarios = new ArrayList<>();

        @Override
        public void enviar(String destinatario, String asunto, String cuerpo) {
            destinatarios.add(destinatario);
        }
    }

    private IPreguntaRepository preguntaRepositorio;
    private IUsuarioRepository usuarioRepositorio;
    private IPreguntaService preguntaService;
    private IAsignacionService asignacionService;
    private EmailEspia emailEspia;

    @BeforeEach
    void inicializar() {
        preguntaRepositorio = new PreguntaRepositoryMemoria();
        usuarioRepositorio = new UsuarioRepositoryMemoria();
        preguntaService = new PreguntaService(preguntaRepositorio, ValidadorFactory.porDefecto(),
                new GeneradorCodigoSecuencial());
        asignacionService = new AsignacionService(preguntaRepositorio, usuarioRepositorio);
        emailEspia = new EmailEspia();
        asignacionService.registrarObservador(new NotificadorEmail(emailEspia));
    }

    private Pregunta crearPendiente(String autor) {
        Pregunta pregunta = preguntaService.crear(PreguntaMother.valida().autorLogin(autor).construir());
        preguntaService.enviarARevision(pregunta.getId(), autor);
        return pregunta;
    }

    @Test
    @DisplayName("HU04: solo aparecen en la bandeja las preguntas Pendientes de revision")
    void listarPendientes() {
        crearPendiente("autor1");
        preguntaService.crear(PreguntaMother.valida().autorLogin("autor1").construir());

        assertEquals(1, asignacionService.listarPendientesDeRevision().size());
    }

    @Test
    @DisplayName("HU04: el autor no aparece como revisor disponible de su propia pregunta")
    void revisoresDisponiblesExcluyenAlAutor() {
        Pregunta pregunta = crearPendiente("autor2");

        boolean contieneAlAutor = asignacionService.listarRevisoresDisponibles(pregunta.getId())
                .stream().anyMatch(u -> u.getLogin().equals("autor2"));

        assertEquals(false, contieneAlAutor);
    }

    @Test
    @DisplayName("HU04: asignar revisores cambia el estado a En revision")
    void asignarCambiaEstado() {
        Pregunta pregunta = crearPendiente("autor1");

        Pregunta actualizada = asignacionService.asignarRevisores(
                pregunta.getId(), List.of("revisor1"), "admin");

        assertEquals(EstadosPregunta.enRevision().getNombre(), actualizada.getEstado().getNombre());
        assertEquals(1, actualizada.getRevisores().size());
    }

    @Test
    @DisplayName("HU04: se envia un correo a cada revisor asignado")
    void notificaPorCorreo() {
        Pregunta pregunta = crearPendiente("autor1");

        asignacionService.asignarRevisores(pregunta.getId(),
                List.of("revisor1", "revisor2"), "admin");

        assertEquals(2, emailEspia.destinatarios.size());
        assertTrue(emailEspia.destinatarios.get(0).contains("@"));
    }

    @Test
    @DisplayName("HU04: se debe asignar al menos un revisor")
    void alMenosUnRevisor() {
        Pregunta pregunta = crearPendiente("autor1");

        assertThrows(ReglaNegocioException.class,
                () -> asignacionService.asignarRevisores(pregunta.getId(), List.of(), "admin"));
    }

    @Test
    @DisplayName("HU04: un usuario sin rol de administrador no puede asignar revisores")
    void soloElAdministradorAsigna() {
        Pregunta pregunta = crearPendiente("autor1");

        assertThrows(ReglaNegocioException.class,
                () -> asignacionService.asignarRevisores(pregunta.getId(), List.of("revisor1"), "autor1"));
    }

    @Test
    @DisplayName("HU04: el autor no puede ser revisor de su propia pregunta")
    void autorNoEsRevisorDeSiMismo() {
        Pregunta pregunta = crearPendiente("autor2");

        assertThrows(ReglaNegocioException.class,
                () -> asignacionService.asignarRevisores(pregunta.getId(), List.of("autor2"), "admin"));
    }

    @Test
    @DisplayName("HU04: no se pueden asignar revisores a una pregunta en Borrador")
    void noAsignaSiEstaEnBorrador() {
        Pregunta pregunta = preguntaService.crear(
                PreguntaMother.valida().autorLogin("autor1").construir());

        assertThrows(ReglaNegocioException.class,
                () -> asignacionService.asignarRevisores(pregunta.getId(), List.of("revisor1"), "admin"));
    }

    @Test
    @DisplayName("Observer: se pueden registrar varios observadores y todos son notificados")
    void variosObservadores() {
        List<String> traza = new ArrayList<>();
        IObservadorAsignacion observador = new IObservadorAsignacion() {
            @Override
            public void alAsignarRevisores(EventoAsignacion evento) {
                traza.add(evento.getPregunta().getId());
            }
        };
        asignacionService.registrarObservador(observador);

        Pregunta pregunta = crearPendiente("autor1");
        asignacionService.asignarRevisores(pregunta.getId(), List.of("revisor1"), "admin");

        assertEquals(1, traza.size());
        assertEquals(1, emailEspia.destinatarios.size());
    }
}
