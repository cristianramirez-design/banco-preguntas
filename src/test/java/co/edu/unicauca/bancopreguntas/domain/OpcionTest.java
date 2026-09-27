package co.edu.unicauca.bancopreguntas.domain;

import co.edu.unicauca.bancopreguntas.domain.model.Opcion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpcionTest {

    @Test
    @DisplayName("Las fabricas estaticas marcan correctamente la respuesta y el distractor")
    void fabricasEstaticas() {
        assertTrue(Opcion.correcta("A").esCorrecta());
        assertFalse(Opcion.distractor("B").esCorrecta());
    }

    @Test
    @DisplayName("El texto se recorta y null se convierte en opcion vacia")
    void normalizaTexto() {
        assertEquals("Respuesta", Opcion.nueva("  Respuesta  ", false).getTexto());
        assertTrue(Opcion.nueva(null, false).estaVacia());
        assertTrue(Opcion.nueva("   ", false).estaVacia());
    }

    @Test
    @DisplayName("Dos opciones son iguales si su texto coincide sin importar mayusculas")
    void igualdadPorTexto() {
        assertEquals(Opcion.correcta("Media"), Opcion.distractor("MEDIA"));
        assertEquals(Opcion.correcta("Media").hashCode(), Opcion.distractor("media").hashCode());
        assertNotEquals(Opcion.correcta("Media"), Opcion.correcta("Moda"));
    }
}
