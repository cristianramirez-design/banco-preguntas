package co.edu.unicauca.bancopreguntas.domain.model.estado;

import java.util.List;

/** Catalogo de estados del ciclo de vida, usado por combos, filtros y leyenda. */
public final class EstadosPregunta {

    private EstadosPregunta() {
    }

    public static IEstadoPregunta borrador() {
        return EstadoBorrador.getInstancia();
    }

    public static IEstadoPregunta pendienteRevision() {
        return EstadoPendienteRevision.getInstancia();
    }

    public static IEstadoPregunta enRevision() {
        return EstadoEnRevision.getInstancia();
    }

    public static IEstadoPregunta aprobada() {
        return EstadoAprobada.getInstancia();
    }

    public static IEstadoPregunta rechazada() {
        return EstadoRechazada.getInstancia();
    }

    /** Los cinco estados del ciclo de vida, en orden. */
    public static List<IEstadoPregunta> valores() {
        return List.of(borrador(), pendienteRevision(), enRevision(), aprobada(), rechazada());
    }

    public static IEstadoPregunta porNombre(String nombre) {
        for (IEstadoPregunta estado : valores()) {
            if (estado.getNombre().equalsIgnoreCase(nombre)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado desconocido: " + nombre);
    }
}
