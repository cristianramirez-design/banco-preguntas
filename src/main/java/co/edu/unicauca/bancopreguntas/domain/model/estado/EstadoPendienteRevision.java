package co.edu.unicauca.bancopreguntas.domain.model.estado;

import co.edu.unicauca.bancopreguntas.domain.excepcion.ReglaNegocioException;

public final class EstadoPendienteRevision implements IEstadoPregunta {

    private static final EstadoPendienteRevision INSTANCIA = new EstadoPendienteRevision();

    private EstadoPendienteRevision() {
    }

    public static EstadoPendienteRevision getInstancia() {
        return INSTANCIA;
    }

    @Override
    public String getNombre() {
        return "Pendiente de revision";
    }

    @Override
    public String getColorHex() {
        return "#F5A623";
    }

    @Override
    public String getDescripcion() {
        return "Listo para asignacion de revisor";
    }

    @Override
    public boolean esEditable() {
        return false;
    }

    @Override
    public IEstadoPregunta enviarARevision() {
        throw new ReglaNegocioException("La pregunta ya se encuentra Pendiente de revision.");
    }

    @Override
    public IEstadoPregunta asignarRevisores() {
        return EstadoEnRevision.getInstancia();
    }

    @Override
    public IEstadoPregunta aprobar() {
        throw new ReglaNegocioException("La pregunta debe tener revisores asignados antes de aprobarse.");
    }

    @Override
    public IEstadoPregunta rechazar() {
        throw new ReglaNegocioException("La pregunta debe tener revisores asignados antes de rechazarse.");
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
