package co.edu.unicauca.bancopreguntas.domain;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.domain.model.Usuario;
import co.edu.unicauca.bancopreguntas.domain.notificacion.EventoAsignacion;
import co.edu.unicauca.bancopreguntas.domain.notificacion.IServicioEmail;
import co.edu.unicauca.bancopreguntas.domain.notificacion.NotificadorBitacora;
import co.edu.unicauca.bancopreguntas.domain.notificacion.NotificadorEmail;
import co.edu.unicauca.bancopreguntas.util.PreguntaMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas de los observadores de la HU-4 y del evento que reciben. */
class NotificacionTest {

    private static class CorreoEspia implements IServicioEmail {
        final List<String[]> enviados = new ArrayList<>();

        @Override
        public void enviar(String destinatario, String asunto, String cuerpo) {
            enviados.add(new String[]{destinatario, asunto, cuerpo});
        }
    }

    private final Pregunta pregunta = PreguntaMother.valida().codigo("A-0231").construir();
    private final Usuario revisor1 = new Usuario("revisor1", "Luisa Munoz", "luisa@unicauca.edu.co", Rol.REVISOR);
    private final Usuario revisor2 = new Usuario("revisor2", "Jorge Vallejo", "jorge@unicauca.edu.co", Rol.REVISOR);

    @Test
    @DisplayName("El evento guarda pregunta, revisores, administrador y momento; su lista es inmutable")
    void eventoAsignacion() {
        List<Usuario> revisores = new ArrayList<>(List.of(revisor1));
        EventoAsignacion evento = new EventoAsignacion(pregunta, revisores, "admin");
        revisores.add(revisor2);

        assertEquals(pregunta, evento.getPregunta());
        assertEquals(1, evento.getRevisores().size(), "copia defensiva");
        assertEquals("admin", evento.getAdministradorLogin());
        assertNotNull(evento.getMomento());
        assertThrows(UnsupportedOperationException.class, () -> evento.getRevisores().add(revisor2));
    }

    @Test
    @DisplayName("NotificadorEmail envia un correo por revisor con los datos de la pregunta (HU-4, CA1)")
    void notificadorEmailUnCorreoPorRevisor() {
        CorreoEspia correo = new CorreoEspia();
        new NotificadorEmail(correo)
                .alAsignarRevisores(new EventoAsignacion(pregunta, List.of(revisor1, revisor2), "admin"));

        assertEquals(2, correo.enviados.size());
        assertEquals("luisa@unicauca.edu.co", correo.enviados.get(0)[0]);
        assertEquals("jorge@unicauca.edu.co", correo.enviados.get(1)[0]);
        String cuerpo = correo.enviados.get(0)[2];
        assertTrue(cuerpo.contains("Luisa Munoz"));
        assertTrue(cuerpo.contains(pregunta.getEnunciado()));
        assertTrue(cuerpo.contains(pregunta.getCompetencia()));
        assertTrue(cuerpo.contains("Código: A-0231"));
        assertTrue(cuerpo.matches("(?s).*Fecha: \\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}.*"), "fecha legible");
        assertTrue(correo.enviados.get(0)[1].contains("A-0231"), "el asunto incluye el código");
    }

    @Test
    @DisplayName("NotificadorBitacora deja una linea por asignacion con los revisores")
    void notificadorBitacora() {
        NotificadorBitacora bitacora = new NotificadorBitacora();
        bitacora.alAsignarRevisores(new EventoAsignacion(pregunta, List.of(revisor1, revisor2), "admin"));

        assertEquals(1, bitacora.getRegistros().size());
        assertTrue(bitacora.getRegistros().get(0).contains("revisor1, revisor2"));
        assertTrue(bitacora.getRegistros().get(0).contains("admin"));
    }
}
