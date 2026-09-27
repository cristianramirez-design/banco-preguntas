package co.edu.unicauca.bancopreguntas.domain;

import co.edu.unicauca.bancopreguntas.domain.busqueda.PaginaResultado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaginaResultadoTest {

    @Test
    @DisplayName("25 elementos en paginas de 10 dan 3 paginas (HU-3, CA1)")
    void calculaTotalPaginas() {
        PaginaResultado<String> pagina = new PaginaResultado<>(List.of("a"), 1, 10, 25);
        assertEquals(3, pagina.getTotalPaginas());
    }

    @Test
    @DisplayName("La primera pagina tiene siguiente pero no anterior")
    void navegacionPrimeraPagina() {
        PaginaResultado<String> pagina = new PaginaResultado<>(List.of("a"), 1, 10, 25);
        assertTrue(pagina.hayPaginaSiguiente());
        assertFalse(pagina.hayPaginaAnterior());
    }

    @Test
    @DisplayName("La ultima pagina tiene anterior pero no siguiente")
    void navegacionUltimaPagina() {
        PaginaResultado<String> pagina = new PaginaResultado<>(List.of("a"), 3, 10, 25);
        assertFalse(pagina.hayPaginaSiguiente());
        assertTrue(pagina.hayPaginaAnterior());
    }

    @Test
    @DisplayName("Sin resultados hay una sola pagina vacia (HU-3, CA3)")
    void sinResultados() {
        PaginaResultado<String> pagina = new PaginaResultado<>(List.of(), 1, 10, 0);
        assertEquals(1, pagina.getTotalPaginas());
        assertTrue(pagina.estaVacia());
        assertFalse(pagina.hayPaginaSiguiente());
    }

    @Test
    @DisplayName("El contenido de la pagina es inmutable")
    void contenidoInmutable() {
        PaginaResultado<String> pagina = new PaginaResultado<>(List.of("a", "b"), 1, 10, 2);
        assertThrows(UnsupportedOperationException.class, () -> pagina.getContenido().add("c"));
    }
}
