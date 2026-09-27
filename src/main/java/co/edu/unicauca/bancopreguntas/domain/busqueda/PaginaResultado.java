package co.edu.unicauca.bancopreguntas.domain.busqueda;

import java.util.List;

/** Resultado paginado generico. */
public class PaginaResultado<T> {

    private final List<T> contenido;
    private final int paginaActual;
    private final int tamanioPagina;
    private final int totalElementos;

    public PaginaResultado(List<T> contenido, int paginaActual, int tamanioPagina, int totalElementos) {
        this.contenido = List.copyOf(contenido);
        this.paginaActual = paginaActual;
        this.tamanioPagina = tamanioPagina;
        this.totalElementos = totalElementos;
    }

    public List<T> getContenido() {
        return contenido;
    }

    public int getPaginaActual() {
        return paginaActual;
    }

    public int getTamanioPagina() {
        return tamanioPagina;
    }

    public int getTotalElementos() {
        return totalElementos;
    }

    public int getTotalPaginas() {
        if (totalElementos == 0) {
            return 1;
        }
        return (int) Math.ceil((double) totalElementos / tamanioPagina);
    }

    public boolean hayPaginaSiguiente() {
        return paginaActual < getTotalPaginas();
    }

    public boolean hayPaginaAnterior() {
        return paginaActual > 1;
    }

    public boolean estaVacia() {
        return contenido.isEmpty();
    }
}
