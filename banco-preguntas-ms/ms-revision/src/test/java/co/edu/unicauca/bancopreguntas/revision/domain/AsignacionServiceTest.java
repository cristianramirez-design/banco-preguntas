package co.edu.unicauca.bancopreguntas.revision.domain;

import co.edu.unicauca.bancopreguntas.revision.domain.excepcion.RecursoNoEncontradoException;
import co.edu.unicauca.bancopreguntas.revision.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Revision;
import co.edu.unicauca.bancopreguntas.revision.domain.servicio.AsignacionService;
import co.edu.unicauca.bancopreguntas.revision.util.Dobles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsignacionServiceTest {

    private Dobles.PendientesMemoria pendientes;
    private Dobles.RevisionesMemoria revisiones;
    private Dobles.PublicadorEspia publicador;
    private AsignacionService servicio;

    @BeforeEach
    void inicializar() {
        pendientes = new Dobles.PendientesMemoria();
        revisiones = new Dobles.RevisionesMemoria();
        publicador = new Dobles.PublicadorEspia();
        servicio = new AsignacionService(pendientes, revisiones, new Dobles.DirectorioFijo(), publicador);
        servicio.registrarPendiente(Dobles.eventoEnvio("p1", "autor1"));
    }

    @Test
    @DisplayName("HU-4, CA3: la pregunta enviada aparece en la bandeja de pendientes")
    void bandejaPendientes() {
        assertEquals(1, servicio.listarPendientes().size());
        assertEquals("A-0231", servicio.listarPendientes().get(0).getCodigo());
    }

    @Test
    @DisplayName("HU-4, CA1: asignar 2 revisores crea las revisiones, saca la pregunta de la bandeja y publica el evento")
    void asignarExitoso() {
        List<Revision> creadas = servicio.asignar("p1", List.of("revisor1", "revisor2"), "admin");

        assertEquals(2, creadas.size());
        assertTrue(servicio.listarPendientes().isEmpty());
        assertEquals(1, publicador.asignaciones.size());
        assertEquals(List.of("revisor1", "revisor2"), publicador.asignaciones.get(0).revisores());
        assertEquals("autor1", publicador.asignaciones.get(0).autorLogin());
    }

    @Test
    @DisplayName("HU-4: se permiten exactamente 3 revisores")
    void asignarTres() {
        assertEquals(3, servicio.asignar("p1", List.of("revisor1", "revisor2", "revisor3"), "admin").size());
    }

    @Test
    @DisplayName("HU-4, CA2: sin revisores aparece 'Debe seleccionar al menos un revisor'")
    void sinRevisores() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> servicio.asignar("p1", List.of(), "admin"));
        assertEquals(AsignacionService.MENSAJE_SIN_REVISORES, e.getMessage());
        assertFalse(servicio.listarPendientes().isEmpty());
        assertTrue(publicador.asignaciones.isEmpty());
    }

    @Test
    @DisplayName("HU-4: con 4 revisores aparece 'Puede asignar máximo 3 revisores' y no se asigna nada")
    void masDeTres() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> servicio.asignar("p1", List.of("revisor1", "revisor2", "revisor3", "revisor4"), "admin"));
        assertEquals(AsignacionService.MENSAJE_MAXIMO_REVISORES, e.getMessage());
        assertTrue(revisiones.datos.isEmpty());
    }

    @Test
    @DisplayName("Los logins repetidos cuentan una sola vez")
    void repetidos() {
        assertEquals(1, servicio.asignar("p1", List.of("revisor1", "REVISOR1 "), "admin").size());
    }

    @Test
    @DisplayName("Solo el administrador puede asignar revisores")
    void soloAdministrador() {
        assertThrows(ReglaNegocioException.class,
                () -> servicio.asignar("p1", List.of("revisor1"), "revisor2"));
    }

    @Test
    @DisplayName("Un usuario sin rol de revisor, o el propio autor, no puede ser asignado")
    void rolRevisorYAutor() {
        assertThrows(ReglaNegocioException.class, () -> servicio.asignar("p1", List.of("autor1"), "admin"));
        assertThrows(ReglaNegocioException.class, () -> servicio.asignar("p1", List.of("nadie"), "admin"));
        servicio.registrarPendiente(Dobles.eventoEnvio("p2", "autor2"));
        assertThrows(ReglaNegocioException.class, () -> servicio.asignar("p2", List.of("autor2"), "admin"));
    }

    @Test
    @DisplayName("No se asignan revisores dos veces ni a una pregunta que no esta pendiente")
    void yaAsignadaOInexistente() {
        servicio.asignar("p1", List.of("revisor1"), "admin");
        assertThrows(ReglaNegocioException.class, () -> servicio.asignar("p1", List.of("revisor2"), "admin"));
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.asignar("p9", List.of("revisor1"), "admin"));
    }

    @Test
    @DisplayName("Una pregunta reenviada vuelve a la bandeja en la ronda 2")
    void reenvio() {
        servicio.asignar("p1", List.of("revisor1"), "admin");
        servicio.registrarPendiente(Dobles.eventoEnvio("p1", "autor1"));
        assertEquals(1, servicio.listarPendientes().size());
        assertEquals(2, servicio.listarPendientes().get(0).getRonda());
    }
}
