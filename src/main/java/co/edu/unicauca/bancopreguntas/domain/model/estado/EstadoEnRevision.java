package co.edu.unicauca.bancopreguntas.domain.model.estado;

import co.edu.unicauca.bancopreguntas.domain.excepcion.ReglaNegocioException;

public final class EstadoEnRevision implements IEstadoPregunta {

    private static final EstadoEnRevision INSTANCIA = new EstadoEnRevision();

    private EstadoEnRevision() {
    }

    public static EstadoEnRevision getInstancia() {
        return INSTANCIA;
    }

    @Override
    public String getNombre() {
        return "En revision";
    }

    @Override
    public String getColorHex() {
        return "#42A5F5";
    }

    @Override
    public String getDescripcion() {
        return "Evaluandose por pares academicos";
    }

    @Override
    public boolean esEditable() {
        return false;
    }

    @Override
    public IEstadoPregunta enviarARevision() {
        throw new ReglaNegocioException("La pregunta ya fue asignada a revisores.");
    }

    @Override
    public IEstadoPregunta asignarRevisores() {
        throw new ReglaNegocioException("La pregunta ya tiene revisores asignados.");
    }

    @Override
    public IEstadoPregunta aprobar() {
        return EstadoAprobada.getInstancia();
    }

    @Override
    public IEstadoPregunta rechazar() {
        return EstadoRechazada.getInstancia();
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
