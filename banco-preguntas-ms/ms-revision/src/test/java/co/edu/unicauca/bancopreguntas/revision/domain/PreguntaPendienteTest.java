package co.edu.unicauca.bancopreguntas.revision.domain;

import co.edu.unicauca.bancopreguntas.revision.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.revision.domain.model.PreguntaPendiente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PreguntaPendienteTest {

    private PreguntaPendiente nueva() {
        return PreguntaPendiente.nueva("p1", "A-0231", "¿Pregunta?", "Tema", "Comp", "autor1", LocalDateTime.now());
    }

    @Test
    @DisplayName("Una pregunta recibida empieza en la ronda 1 sin revisores")
    void nuevaPendiente() {
        PreguntaPendiente p = nueva();
        assertEquals(1, p.getRonda());
        assertFalse(p.isAsignada());
    }

    @Test
    @DisplayName("Marcar asignada guarda el administrador y no se puede repetir")
    void marcarAsignada() {
        PreguntaPendiente p = nueva();
        p.marcarAsignada("admin");
        assertTrue(p.isAsignada());
        assertEquals("admin", p.getAdministradorLogin());
        assertThrows(ReglaNegocioException.class, () -> p.marcarAsignada("admin"));
    }

    @Test
    @DisplayName("Al reenviar una pregunta corregida empieza una ronda nueva sin revisores")
    void reenviar() {
        PreguntaPendiente p = nueva();
        p.marcarAsignada("admin");
        p.reenviar("A-0231", "¿Pregunta corregida?", "Tema", "Comp", LocalDateTime.now());
        assertEquals(2, p.getRonda());
        assertFalse(p.isAsignada());
        assertNull(p.getAdministradorLogin());
        assertEquals("¿Pregunta corregida?", p.getEnunciado());
    }

    @Test
    @DisplayName("Un evento de envio repetido no cambia la ronda (idempotencia)")
    void reenvioRepetido() {
        PreguntaPendiente p = nueva();
        p.reenviar("A-0231", "¿Pregunta?", "Tema", "Comp", LocalDateTime.now());
        assertEquals(1, p.getRonda());
    }
}
