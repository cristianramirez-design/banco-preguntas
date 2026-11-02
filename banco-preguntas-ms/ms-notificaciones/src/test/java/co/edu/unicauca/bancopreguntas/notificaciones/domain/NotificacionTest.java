package co.edu.unicauca.bancopreguntas.notificaciones.domain;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.evento.PreguntaEvaluadaEvento;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.model.EstadoEnvio;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.model.Mensaje;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.model.Notificacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacionTest {

    private final Mensaje mensaje = new Mensaje("revisor1", "Asunto", "Cuerpo", "TIPO");

    @Test
    @DisplayName("Una notificacion enviada guarda el correo y no tiene error")
    void enviada() {
        Notificacion n = Notificacion.enviada(mensaje, "r1@unicauca.edu.co");
        assertEquals(EstadoEnvio.ENVIADA, n.getEstado());
        assertEquals("r1@unicauca.edu.co", n.getDestinatarioEmail());
        assertEquals("Asunto", n.getAsunto());
        assertNull(n.getDetalleError());
        assertNotNull(n.getId());
        assertNotNull(n.getFecha());
    }

    @Test
    @DisplayName("Una notificacion fallida conserva el motivo")
    void fallida() {
        Notificacion n = Notificacion.fallida(mensaje, null, "Sin correo");
        assertEquals(EstadoEnvio.FALLIDA, n.getEstado());
        assertEquals("Sin correo", n.getDetalleError());
    }

    @Test
    @DisplayName("El evento sabe si la pregunta fue aprobada")
    void eventoAprobada() {
        assertTrue(new PreguntaEvaluadaEvento("p", "c", "e", "a", "ad", "APROBADA", List.of(), LocalDateTime.now()).aprobada());
        assertFalse(new PreguntaEvaluadaEvento("p", "c", "e", "a", "ad", "RECHAZADA", List.of(), LocalDateTime.now()).aprobada());
    }
}
