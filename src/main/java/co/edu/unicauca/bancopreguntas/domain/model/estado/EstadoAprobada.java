package co.edu.unicauca.bancopreguntas.domain.model.estado;

import co.edu.unicauca.bancopreguntas.domain.excepcion.ReglaNegocioException;

/** Estado terminal: la pregunta ya forma parte del banco. */
public final class EstadoAprobada implements IEstadoPregunta {

    private static final EstadoAprobada INSTANCIA = new EstadoAprobada();

    private EstadoAprobada() {
    }

    public static EstadoAprobada getInstancia() {
        return INSTANCIA;
    }

    @Override
    public String getNombre() {
        return "Aprobada";
    }

    @Override
    public String getColorHex() {
        return "#66BB6A";
    }

    @Override
    public String getDescripcion() {
        return "Pregunta aprobada para el banco";
    }

    @Override
    public boolean esEditable() {
        return false;
    }

    @Override
    public IEstadoPregunta enviarARevision() {
        throw new ReglaNegocioException("Una pregunta Aprobada no vuelve a revision.");
    }

    @Override
    public IEstadoPregunta asignarRevisores() {
        throw new ReglaNegocioException("Una pregunta Aprobada no admite nuevos revisores.");
    }

    @Override
    public IEstadoPregunta aprobar() {
        throw new ReglaNegocioException("La pregunta ya esta Aprobada.");
    }

    @Override
    public IEstadoPregunta rechazar() {
        throw new ReglaNegocioException("Una pregunta Aprobada no puede rechazarse.");
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
