package co.edu.unicauca.bancopreguntas.domain;

import co.edu.unicauca.bancopreguntas.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.util.PreguntaMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PreguntaTest {

    @Test
    @DisplayName("El Builder crea la pregunta con cinco opciones y estado Borrador")
    void builderCreaPreguntaCompleta() {
        Pregunta pregunta = PreguntaMother.valida().construir();

        assertEquals(5, pregunta.getOpciones().size());
        assertEquals(4, pregunta.getDistractores().size());
        assertEquals(0, pregunta.getIndiceRespuestaCorrecta());
        assertTrue(pregunta.getRespuestaCorrecta().esCorrecta());
        assertEquals(EstadosPregunta.borrador().getNombre(), pregunta.getEstado().getNombre());
    }

    @Test
    @DisplayName("Solo la opcion marcada queda como respuesta correcta")
    void marcaUnaSolaRespuestaCorrecta() {
        Pregunta pregunta = PreguntaMother.valida()
                .opcionesConCorrecta(PreguntaMother.OPCIONES, 2).construir();

        assertEquals(2, pregunta.getIndiceRespuestaCorrecta());
        assertEquals(PreguntaMother.OPCIONES.get(2), pregunta.getRespuestaCorrecta().getTexto());
    }

    @Test
    @DisplayName("El Builder recorta los espacios de los campos de texto")
    void builderNormalizaTexto() {
        Pregunta pregunta = PreguntaMother.valida().tema("   Probabilidad   ").construir();
        assertEquals("Probabilidad", pregunta.getTema());
    }

    @Test
    @DisplayName("perteneceA reconoce al autor sin distinguir mayusculas")
    void perteneceAlAutor() {
        Pregunta pregunta = PreguntaMother.valida().autorLogin("autor1").construir();
        assertTrue(pregunta.perteneceA("AUTOR1"));
        assertFalse(pregunta.perteneceA("autor2"));
    }

    @Test
    @DisplayName("aBuilder produce una copia editable con el mismo id y codigo")
    void copiaEditable() {
        Pregunta original = PreguntaMother.valida().codigo("A-0231").construir();
        Pregunta copia = original.aBuilder().tema("Probabilidad").construir();

        assertEquals(original.getId(), copia.getId());
        assertEquals("A-0231", copia.getCodigo());
        assertEquals("Probabilidad", copia.getTema());
    }

    @Test
    @DisplayName("HU-2: de Borrador pasa a Pendiente de revision y registra la fecha de envio")
    void transicionABorradorPendiente() {
        Pregunta pregunta = PreguntaMother.valida().construir();
        pregunta.enviarARevision();

        assertEquals(EstadosPregunta.pendienteRevision().getNombre(), pregunta.getEstado().getNombre());
        assertFalse(pregunta.esEditable());
        assertTrue(pregunta.getFechaEnvioRevision() != null);
    }

    @Test
    @DisplayName("No se puede enviar dos veces a revision la misma pregunta")
    void transicionInvalidaDesdePendiente() {
        Pregunta pregunta = PreguntaMother.valida().construir();
        pregunta.enviarARevision();
        assertThrows(ReglaNegocioException.class, pregunta::enviarARevision);
    }

    @Test
    @DisplayName("No se pueden asignar revisores a una pregunta en Borrador")
    void noSeAsignanRevisoresEnBorrador() {
        Pregunta pregunta = PreguntaMother.valida().construir();
        assertThrows(ReglaNegocioException.class, () -> pregunta.asignarRevisores(List.of("revisor1")));
    }

    @Test
    @DisplayName("HU-4: de Pendiente de revision pasa a En revision con revisores")
    void transicionAEnRevision() {
        Pregunta pregunta = PreguntaMother.valida().construir();
        pregunta.enviarARevision();
        pregunta.asignarRevisores(List.of("revisor1", "revisor2"));

        assertEquals(EstadosPregunta.enRevision().getNombre(), pregunta.getEstado().getNombre());
        assertEquals(2, pregunta.getRevisores().size());
    }

    @Test
    @DisplayName("Desde En revision la pregunta puede aprobarse")
    void transicionAAprobada() {
        Pregunta pregunta = PreguntaMother.valida().construir();
        pregunta.enviarARevision();
        pregunta.asignarRevisores(List.of("revisor1"));
        pregunta.aprobar();

        assertEquals(EstadosPregunta.aprobada().getNombre(), pregunta.getEstado().getNombre());
        assertFalse(pregunta.esEditable());
    }

    @Test
    @DisplayName("Una pregunta Rechazada vuelve a ser editable y puede reenviarse")
    void transicionARechazadaYReenvio() {
        Pregunta pregunta = PreguntaMother.valida().construir();
        pregunta.enviarARevision();
        pregunta.asignarRevisores(List.of("revisor1"));
        pregunta.rechazar();

        assertEquals(EstadosPregunta.rechazada().getNombre(), pregunta.getEstado().getNombre());
        assertTrue(pregunta.esEditable());

        pregunta.enviarARevision();
        assertEquals(EstadosPregunta.pendienteRevision().getNombre(), pregunta.getEstado().getNombre());
    }

    @Test
    @DisplayName("Una pregunta Aprobada es un estado terminal")
    void aprobadaEsTerminal() {
        Pregunta pregunta = PreguntaMother.valida().construir();
        pregunta.enviarARevision();
        pregunta.asignarRevisores(List.of("revisor1"));
        pregunta.aprobar();

        assertThrows(ReglaNegocioException.class, pregunta::enviarARevision);
        assertThrows(ReglaNegocioException.class, pregunta::rechazar);
    }

    @Test
    @DisplayName("HU-2, CA3: los cinco estados tienen nombre y color distintos")
    void cincoEstadosConColoresDistintos() {
        assertEquals(5, EstadosPregunta.valores().size());
        assertEquals(5L, EstadosPregunta.valores().stream()
                .map(estado -> estado.getColorHex())
                .distinct()
                .count());
    }

    @Test
    @DisplayName("Los estados se pueden recuperar por nombre")
    void estadoPorNombre() {
        assertEquals(EstadosPregunta.aprobada(), EstadosPregunta.porNombre("Aprobada"));
        assertThrows(IllegalArgumentException.class, () -> EstadosPregunta.porNombre("Inexistente"));
    }
}
