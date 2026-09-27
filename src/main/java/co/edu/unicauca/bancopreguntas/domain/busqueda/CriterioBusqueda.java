package co.edu.unicauca.bancopreguntas.domain.busqueda;

import co.edu.unicauca.bancopreguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.domain.model.estado.IEstadoPregunta;

/**
 * Objeto de criterio que la capa de presentacion arma y el dominio traduce
 * a una combinacion de filtros (Strategy + Composite).
 * Cubre los filtros por Estado, Tema, Subtema y Competencia de la HU-3, CA2.
 */
public class CriterioBusqueda {

    private String autorLogin;
    private IEstadoPregunta estado;
    private String tema;
    private String subtema;
    private String competencia;
    private NivelDificultad nivelDificultad;
    private String texto;

    public CriterioBusqueda autor(String autorLogin) {
        this.autorLogin = autorLogin;
        return this;
    }

    public CriterioBusqueda estado(IEstadoPregunta estado) {
        this.estado = estado;
        return this;
    }

    public CriterioBusqueda tema(String tema) {
        this.tema = tema;
        return this;
    }

    public CriterioBusqueda subtema(String subtema) {
        this.subtema = subtema;
        return this;
    }

    public CriterioBusqueda competencia(String competencia) {
        this.competencia = competencia;
        return this;
    }

    public CriterioBusqueda nivelDificultad(NivelDificultad nivel) {
        this.nivelDificultad = nivel;
        return this;
    }

    public CriterioBusqueda texto(String texto) {
        this.texto = texto;
        return this;
    }

    public IFiltroPregunta aFiltro() {
        FiltroCompuestoY compuesto = new FiltroCompuestoY();
        if (noVacio(autorLogin)) {
            compuesto.agregar(new FiltroPorAutor(autorLogin));
        }
        if (estado != null) {
            compuesto.agregar(new FiltroPorEstado(estado));
        }
        if (noVacio(tema)) {
            compuesto.agregar(new FiltroPorTema(tema));
        }
        if (noVacio(subtema)) {
            compuesto.agregar(new FiltroPorSubtema(subtema));
        }
        if (noVacio(competencia)) {
            compuesto.agregar(new FiltroPorCompetencia(competencia));
        }
        if (nivelDificultad != null) {
            compuesto.agregar(new FiltroPorDificultad(nivelDificultad));
        }
        if (noVacio(texto)) {
            compuesto.agregar(new FiltroPorTexto(texto));
        }
        return compuesto;
    }

    private static boolean noVacio(String valor) {
        return valor != null && !valor.isBlank();
    }
}
