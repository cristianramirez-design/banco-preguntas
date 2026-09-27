package co.edu.unicauca.bancopreguntas.domain.model.estado;

import co.edu.unicauca.bancopreguntas.domain.excepcion.ReglaNegocioException;

/** La pregunta se devuelve al autor para correcciones y vuelve a ser editable. */
public final class EstadoRechazada implements IEstadoPregunta {

    private static final EstadoRechazada INSTANCIA = new EstadoRechazada();

    private EstadoRechazada() {
    }

    public static EstadoRechazada getInstancia() {
        return INSTANCIA;
    }

    @Override
    public String getNombre() {
        return "Rechazada";
    }

    @Override
    public String getColorHex() {
        return "#EF5350";
    }

    @Override
    public String getDescripcion() {
        return "Devuelta para correcciones";
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
                "La pregunta rechazada debe corregirse y volver a enviarse a revision.");
    }

    @Override
    public IEstadoPregunta aprobar() {
        throw new ReglaNegocioException("La pregunta rechazada debe corregirse primero.");
    }

    @Override
    public IEstadoPregunta rechazar() {
        throw new ReglaNegocioException("La pregunta ya esta Rechazada.");
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
