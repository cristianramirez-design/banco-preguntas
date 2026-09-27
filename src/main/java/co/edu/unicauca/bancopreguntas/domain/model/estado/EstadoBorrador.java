package co.edu.unicauca.bancopreguntas.domain.model.estado;

import co.edu.unicauca.bancopreguntas.domain.excepcion.ReglaNegocioException;

/** Patron GoF Singleton: el estado no tiene datos propios, basta una instancia. */
public final class EstadoBorrador implements IEstadoPregunta {

    private static final EstadoBorrador INSTANCIA = new EstadoBorrador();

    private EstadoBorrador() {
    }

    public static EstadoBorrador getInstancia() {
        return INSTANCIA;
    }

    @Override
    public String getNombre() {
        return "Borrador";
    }

    @Override
    public String getColorHex() {
        return "#9E9E9E";
    }

    @Override
    public String getDescripcion() {
        return "Edicion inicial del profesor";
    }

    @Override
    public boolean esEditable() {
        return true;
    }

    @Override
    public IEstadoPregunta enviarARevision() {
        return EstadoPendienteRevision.getInstancia();
    }

    @Override
    public IEstadoPregunta asignarRevisores() {
        throw new ReglaNegocioException(
                "No se pueden asignar revisores a una pregunta en estado Borrador.");
    }

    @Override
    public IEstadoPregunta aprobar() {
        throw new ReglaNegocioException("Una pregunta en Borrador no puede aprobarse.");
    }

    @Override
    public IEstadoPregunta rechazar() {
        throw new ReglaNegocioException("Una pregunta en Borrador no puede rechazarse.");
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
