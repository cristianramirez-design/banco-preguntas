package co.edu.unicauca.bancopreguntas.notificaciones.domain;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.evento.PreguntaEvaluadaEvento;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.evento.RevisoresAsignadosEvento;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.model.Mensaje;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.plantilla.PlantillaNotificacion;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.plantilla.PlantillaPreguntaEvaluada;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.plantilla.PlantillaRevisoresAsignados;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas del patron Template Method. */
class PlantillasTest {

    static RevisoresAsignadosEvento asignacion(List<String> revisores) {
        return new RevisoresAsignadosEvento("p1", "A-0231", "¿Cual es la media?", "Estadistica",
                "Razonamiento Cuantitativo", "autor1", revisores, "admin", LocalDateTime.now());
    }

    static PreguntaEvaluadaEvento evaluacion(String resultado) {
        return new PreguntaEvaluadaEvento("p1", "A-0231", "¿Cual es la media?", "autor1", "admin", resultado,
                List.of(new PreguntaEvaluadaEvento.Observacion("revisor1", "APROBAR", "Bien redactada"),
                        new PreguntaEvaluadaEvento.Observacion("revisor2", "RECHAZAR", "La opcion B tambien es valida")),
                LocalDateTime.now());
    }

    @Test
    @DisplayName("HU-4: un correo por revisor asignado y uno para el autor")
    void asignacionGeneraUnCorreoPorRevisorYAutor() {
        List<Mensaje> m = new PlantillaRevisoresAsignados().generar(asignacion(List.of("revisor1", "revisor2")));

        assertEquals(3, m.size());
        assertEquals("revisor1", m.get(0).destinatarioLogin());
        assertTrue(m.get(0).asunto().contains("A-0231"));
        assertTrue(m.get(0).cuerpo().contains("Mis revisiones"));
        assertEquals("autor1", m.get(2).destinatarioLogin());
        assertTrue(m.get(2).cuerpo().contains("2 revisor(es)"));
    }

    @Test
    @DisplayName("El algoritmo comun agrega saludo y firma y no repite destinatarios")
    void pasosComunes() {
        List<Mensaje> m = new PlantillaRevisoresAsignados().generar(asignacion(List.of("revisor1", "revisor1")));

        assertEquals(2, m.size());
        assertTrue(m.get(0).cuerpo().startsWith("Hola revisor1,"));
        assertTrue(m.get(0).cuerpo().endsWith(PlantillaNotificacion.FIRMA));
        assertEquals(PlantillaRevisoresAsignados.TIPO, m.get(0).tipo());
    }

    @Test
    @DisplayName("HU-5: el resultado llega al autor, al administrador y a cada revisor con las observaciones")
    void evaluacionNotificaATodosLosInvolucrados() {
        List<Mensaje> m = new PlantillaPreguntaEvaluada().generar(evaluacion("RECHAZADA"));

        assertEquals(List.of("autor1", "admin", "revisor1", "revisor2"),
                m.stream().map(Mensaje::destinatarioLogin).toList());
        assertTrue(m.get(0).asunto().endsWith("RECHAZADA"));
        assertTrue(m.get(0).cuerpo().contains("La opcion B tambien es valida"));
        assertTrue(m.get(0).cuerpo().contains("enviarla de nuevo"));
        assertFalse(m.get(1).cuerpo().contains("enviarla de nuevo"));
    }

    @Test
    @DisplayName("HU-5: si se aprueba, el autor no recibe la invitacion a corregir")
    void aprobada() {
        Mensaje autor = new PlantillaPreguntaEvaluada().generar(evaluacion("APROBADA")).get(0);
        assertTrue(autor.asunto().endsWith("APROBADA"));
        assertFalse(autor.cuerpo().contains("enviarla de nuevo"));
    }
}
