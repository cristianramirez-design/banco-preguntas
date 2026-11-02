package co.edu.unicauca.bancopreguntas.revision.domain;

import co.edu.unicauca.bancopreguntas.revision.domain.model.Decision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.PreguntaPendiente;
import co.edu.unicauca.bancopreguntas.revision.domain.model.ResultadoRevision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Revision;
import co.edu.unicauca.bancopreguntas.revision.domain.politica.IPoliticaDecision;
import co.edu.unicauca.bancopreguntas.revision.domain.politica.PoliticaDecisionFactory;
import co.edu.unicauca.bancopreguntas.revision.domain.politica.PoliticaMayoria;
import co.edu.unicauca.bancopreguntas.revision.domain.politica.PoliticaUnanimidad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas de las estrategias de decision (Strategy) y de su fabrica (Factory Method). */
class PoliticaDecisionTest {

    private final PreguntaPendiente pregunta =
            PreguntaPendiente.nueva("p1", "A-0231", "¿P?", "T", "C", "autor1", LocalDateTime.now());

    private Revision revision(Decision decision) {
        Revision r = Revision.asignar(pregunta, "revisor");
        if (decision != null) {
            r.evaluar(decision, decision == Decision.RECHAZAR ? "Observacion" : "");
        }
        return r;
    }

    @Test
    @DisplayName("Mientras falte un revisor por evaluar no hay resultado")
    void faltaUnRevisor() {
        List<Revision> ronda = List.of(revision(Decision.APROBAR), revision(null));
        assertTrue(new PoliticaUnanimidad().decidir(ronda).isEmpty());
        assertTrue(new PoliticaMayoria().decidir(ronda).isEmpty());
        assertTrue(new PoliticaUnanimidad().decidir(List.of()).isEmpty());
    }

    @Test
    @DisplayName("Unanimidad: todos aprueban -> APROBADA; un rechazo -> RECHAZADA")
    void unanimidad() {
        IPoliticaDecision p = new PoliticaUnanimidad();
        assertEquals(Optional.of(ResultadoRevision.APROBADA),
                p.decidir(List.of(revision(Decision.APROBAR), revision(Decision.APROBAR))));
        assertEquals(Optional.of(ResultadoRevision.RECHAZADA),
                p.decidir(List.of(revision(Decision.APROBAR), revision(Decision.RECHAZAR), revision(Decision.APROBAR))));
    }

    @Test
    @DisplayName("Mayoria: 2 de 3 aprueban -> APROBADA; empate -> RECHAZADA")
    void mayoria() {
        IPoliticaDecision p = new PoliticaMayoria();
        assertEquals(Optional.of(ResultadoRevision.APROBADA),
                p.decidir(List.of(revision(Decision.APROBAR), revision(Decision.RECHAZAR), revision(Decision.APROBAR))));
        assertEquals(Optional.of(ResultadoRevision.RECHAZADA),
                p.decidir(List.of(revision(Decision.APROBAR), revision(Decision.RECHAZAR))));
    }

    @Test
    @DisplayName("La fabrica crea la politica configurada; por defecto unanimidad")
    void fabrica() {
        assertInstanceOf(PoliticaUnanimidad.class, PoliticaDecisionFactory.crear(null));
        assertInstanceOf(PoliticaUnanimidad.class, PoliticaDecisionFactory.crear("unanimidad"));
        assertInstanceOf(PoliticaMayoria.class, PoliticaDecisionFactory.crear("MAYORIA"));
        assertThrows(IllegalArgumentException.class, () -> PoliticaDecisionFactory.crear("sorteo"));
    }
}
