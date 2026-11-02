package co.edu.unicauca.bancopreguntas.revision.domain;

import co.edu.unicauca.bancopreguntas.revision.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Decision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.EstadoRevision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.PreguntaPendiente;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Revision;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RevisionTest {

    private Revision nueva() {
        PreguntaPendiente p = PreguntaPendiente.nueva("p1", "A-0231", "¿Pregunta?", "Tema", "Comp", "autor1",
                LocalDateTime.now());
        return Revision.asignar(p, "revisor1");
    }

    @Test
    @DisplayName("Una revision recien asignada esta en estado ASIGNADA y copia los datos de la pregunta")
    void asignar() {
        Revision r = nueva();
        assertEquals(EstadoRevision.ASIGNADA, r.getEstado());
        assertEquals("A-0231", r.getCodigoPregunta());
        assertEquals(1, r.getRonda());
        assertFalse(r.estaEvaluada());
        assertNotNull(r.getId());
    }

    @Test
    @DisplayName("HU-5: aprobar con observaciones opcionales")
    void aprobar() {
        Revision r = nueva();
        r.evaluar(Decision.APROBAR, null);
        assertEquals(EstadoRevision.APROBADA, r.getEstado());
        assertEquals("", r.getObservaciones());
        assertNotNull(r.getFechaEvaluacion());
    }

    @Test
    @DisplayName("HU-5: rechazar exige observaciones para el autor")
    void rechazarSinObservaciones() {
        Revision r = nueva();
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> r.evaluar(Decision.RECHAZAR, "  "));
        assertEquals(Revision.MENSAJE_OBSERVACIONES_OBLIGATORIAS, e.getMessage());
        assertEquals(EstadoRevision.ASIGNADA, r.getEstado());
    }

    @Test
    @DisplayName("HU-5: rechazar con observaciones guarda el texto sin espacios sobrantes")
    void rechazar() {
        Revision r = nueva();
        r.evaluar(Decision.RECHAZAR, "  El distractor C es ambiguo. ");
        assertEquals(EstadoRevision.RECHAZADA, r.getEstado());
        assertEquals("El distractor C es ambiguo.", r.getObservaciones());
    }

    @Test
    @DisplayName("Una revision no se puede evaluar dos veces ni sin decision")
    void evaluarDosVeces() {
        Revision r = nueva();
        assertThrows(ReglaNegocioException.class, () -> r.evaluar(null, "x"));
        r.evaluar(Decision.APROBAR, "Bien");
        assertThrows(ReglaNegocioException.class, () -> r.evaluar(Decision.RECHAZAR, "Cambio de opinion"));
    }

    @Test
    @DisplayName("perteneceA compara el login sin importar mayusculas")
    void pertenece() {
        assertTrue(nueva().perteneceA("REVISOR1"));
        assertFalse(nueva().perteneceA("revisor2"));
    }
}
