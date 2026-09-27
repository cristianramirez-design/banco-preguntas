package co.edu.unicauca.bancopreguntas.domain;

import co.edu.unicauca.bancopreguntas.domain.busqueda.CriterioBusqueda;
import co.edu.unicauca.bancopreguntas.domain.busqueda.FiltroCompuestoY;
import co.edu.unicauca.bancopreguntas.domain.busqueda.FiltroPorAutor;
import co.edu.unicauca.bancopreguntas.domain.busqueda.FiltroPorCompetencia;
import co.edu.unicauca.bancopreguntas.domain.busqueda.FiltroPorDificultad;
import co.edu.unicauca.bancopreguntas.domain.busqueda.FiltroPorEstado;
import co.edu.unicauca.bancopreguntas.domain.busqueda.FiltroPorSubtema;
import co.edu.unicauca.bancopreguntas.domain.busqueda.FiltroPorTema;
import co.edu.unicauca.bancopreguntas.domain.busqueda.FiltroPorTexto;
import co.edu.unicauca.bancopreguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.util.PreguntaMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas de cada estrategia de filtrado (Strategy) y de su composicion (Composite). */
class FiltrosBusquedaTest {

    private final Pregunta pregunta = PreguntaMother.valida().codigo("A-0231").construir();

    @Test
    @DisplayName("Filtro por autor, sin distinguir mayusculas")
    void porAutor() {
        assertTrue(new FiltroPorAutor("AUTOR1").cumple(pregunta));
        assertFalse(new FiltroPorAutor("autor2").cumple(pregunta));
    }

    @Test
    @DisplayName("Filtro por estado")
    void porEstado() {
        assertTrue(new FiltroPorEstado(EstadosPregunta.borrador()).cumple(pregunta));
        assertFalse(new FiltroPorEstado(EstadosPregunta.aprobada()).cumple(pregunta));
    }

    @Test
    @DisplayName("Filtros por tema, subtema y competencia")
    void porClasificacion() {
        assertTrue(new FiltroPorTema("estadistica").cumple(pregunta));
        assertFalse(new FiltroPorTema("probabilidad").cumple(pregunta));
        assertTrue(new FiltroPorSubtema("tendencia central").cumple(pregunta));
        assertFalse(new FiltroPorSubtema("eventos").cumple(pregunta));
        assertTrue(new FiltroPorCompetencia("Razonamiento Cuantitativo").cumple(pregunta));
        assertFalse(new FiltroPorCompetencia("Lectura Critica").cumple(pregunta));
    }

    @Test
    @DisplayName("Filtro por nivel de dificultad")
    void porDificultad() {
        assertTrue(new FiltroPorDificultad(NivelDificultad.MEDIA).cumple(pregunta));
        assertFalse(new FiltroPorDificultad(NivelDificultad.ALTA).cumple(pregunta));
    }

    @Test
    @DisplayName("Busqueda libre por codigo o contenido; texto vacio acepta todo")
    void porTexto() {
        assertTrue(new FiltroPorTexto("a-0231").cumple(pregunta));
        assertTrue(new FiltroPorTexto("desempeno").cumple(pregunta));
        assertTrue(new FiltroPorTexto("").cumple(pregunta));
        assertFalse(new FiltroPorTexto("fotosintesis").cumple(pregunta));
    }

    @Test
    @DisplayName("El filtro compuesto exige que se cumplan todos sus filtros (Composite)")
    void compuestoY() {
        FiltroCompuestoY compuesto = new FiltroCompuestoY();
        assertTrue(compuesto.cumple(pregunta), "sin filtros acepta todo");
        compuesto.agregar(new FiltroPorAutor("autor1"));
        compuesto.agregar(new FiltroPorTema("estadistica"));
        assertTrue(compuesto.cumple(pregunta));
        compuesto.agregar(new FiltroPorDificultad(NivelDificultad.BAJA));
        assertFalse(compuesto.cumple(pregunta));
    }

    @Test
    @DisplayName("CriterioBusqueda traduce los campos llenos a filtros e ignora los vacios")
    void criterioBusqueda() {
        assertTrue(new CriterioBusqueda().aFiltro().cumple(pregunta));
        assertTrue(new CriterioBusqueda().autor("autor1").tema("").subtema(null)
                .estado(EstadosPregunta.borrador()).aFiltro().cumple(pregunta));
        assertFalse(new CriterioBusqueda().autor("autor1").competencia("Ingles")
                .aFiltro().cumple(pregunta));
        assertFalse(new CriterioBusqueda().nivelDificultad(NivelDificultad.ALTA)
                .aFiltro().cumple(pregunta));
        assertFalse(new CriterioBusqueda().texto("no-existe").aFiltro().cumple(pregunta));
    }
}
