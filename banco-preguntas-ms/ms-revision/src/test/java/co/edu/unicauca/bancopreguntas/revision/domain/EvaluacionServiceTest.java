package co.edu.unicauca.bancopreguntas.revision.domain;

import co.edu.unicauca.bancopreguntas.revision.domain.excepcion.RecursoNoEncontradoException;
import co.edu.unicauca.bancopreguntas.revision.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Decision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.EstadoRevision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Revision;
import co.edu.unicauca.bancopreguntas.revision.domain.politica.PoliticaUnanimidad;
import co.edu.unicauca.bancopreguntas.revision.domain.servicio.AsignacionService;
import co.edu.unicauca.bancopreguntas.revision.domain.servicio.EvaluacionService;
import co.edu.unicauca.bancopreguntas.revision.util.Dobles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvaluacionServiceTest {

    private Dobles.PublicadorEspia publicador;
    private AsignacionService asignacion;
    private EvaluacionService evaluacion;
    private List<Revision> revisiones;

    @BeforeEach
    void inicializar() {
        Dobles.PendientesMemoria pendientes = new Dobles.PendientesMemoria();
        Dobles.RevisionesMemoria repo = new Dobles.RevisionesMemoria();
        publicador = new Dobles.PublicadorEspia();
        asignacion = new AsignacionService(pendientes, repo, new Dobles.DirectorioFijo(), publicador);
        evaluacion = new EvaluacionService(repo, pendientes, new PoliticaUnanimidad(), publicador);
        asignacion.registrarPendiente(Dobles.eventoEnvio("p1", "autor1"));
        revisiones = asignacion.asignar("p1", List.of("revisor1", "revisor2"), "admin");
    }

    @Test
    @DisplayName("HU-5, CA1: el revisor ve solo las preguntas que le asignaron")
    void listarAsignadas() {
        assertEquals(1, evaluacion.listarAsignadas("revisor1").size());
        assertTrue(evaluacion.listarAsignadas("revisor3").isEmpty());
    }

    @Test
    @DisplayName("HU-5: con un solo revisor evaluado todavia no hay resultado")
    void primerRevisor() {
        evaluacion.evaluar(revisiones.get(0).getId(), "revisor1", Decision.APROBAR, "Bien");
        assertTrue(publicador.evaluaciones.isEmpty());
    }

    @Test
    @DisplayName("HU-5: cuando todos aprueban se publica PreguntaEvaluada APROBADA con las observaciones")
    void todosAprueban() {
        evaluacion.evaluar(revisiones.get(0).getId(), "revisor1", Decision.APROBAR, "Bien");
        evaluacion.evaluar(revisiones.get(1).getId(), "revisor2", Decision.APROBAR, "Clara");

        assertEquals(1, publicador.evaluaciones.size());
        assertEquals("APROBADA", publicador.evaluaciones.get(0).resultado());
        assertEquals("autor1", publicador.evaluaciones.get(0).autorLogin());
        assertEquals("admin", publicador.evaluaciones.get(0).administradorLogin());
        assertEquals(2, publicador.evaluaciones.get(0).observaciones().size());
    }

    @Test
    @DisplayName("HU-5: un rechazo deja la pregunta RECHAZADA")
    void unRechazo() {
        evaluacion.evaluar(revisiones.get(0).getId(), "revisor1", Decision.APROBAR, "");
        evaluacion.evaluar(revisiones.get(1).getId(), "revisor2", Decision.RECHAZAR, "Falta bibliografia actualizada");

        assertEquals("RECHAZADA", publicador.evaluaciones.get(0).resultado());
    }

    @Test
    @DisplayName("Solo el revisor asignado puede evaluar, y la revision debe existir")
    void soloRevisorAsignado() {
        assertThrows(ReglaNegocioException.class,
                () -> evaluacion.evaluar(revisiones.get(0).getId(), "revisor2", Decision.APROBAR, ""));
        assertThrows(RecursoNoEncontradoException.class,
                () -> evaluacion.evaluar("no-existe", "revisor1", Decision.APROBAR, ""));
    }

    @Test
    @DisplayName("HU-5: el autor puede leer las observaciones de los revisores")
    void observacionesParaElAutor() {
        evaluacion.evaluar(revisiones.get(0).getId(), "revisor1", Decision.RECHAZAR, "Ajustar el contexto");

        List<Revision> obs = evaluacion.observacionesDePregunta("p1");
        assertEquals(1, obs.size());
        assertEquals("Ajustar el contexto", obs.get(0).getObservaciones());
        assertEquals(EstadoRevision.RECHAZADA, obs.get(0).getEstado());
    }
}
