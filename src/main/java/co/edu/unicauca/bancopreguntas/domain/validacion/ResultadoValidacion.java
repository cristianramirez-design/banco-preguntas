package co.edu.unicauca.bancopreguntas.domain.validacion;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ResultadoValidacion {

    private final List<ErrorValidacion> errores = new ArrayList<>();

    public void agregarError(ErrorValidacion error) {
        errores.add(error);
    }

    public boolean esValido() {
        return errores.isEmpty();
    }

    public List<ErrorValidacion> getErrores() {
        return List.copyOf(errores);
    }

    /** Mensajes listos para mostrar al usuario. */
    public List<String> getMensajes() {
        List<String> mensajes = new ArrayList<>();
        for (ErrorValidacion error : errores) {
            mensajes.add(error.getMensaje());
        }
        return mensajes;
    }

    /** Campos que la vista debe resaltar en rojo. */
    public Set<CampoPregunta> getCamposInvalidos() {
        Set<CampoPregunta> campos = new LinkedHashSet<>();
        for (ErrorValidacion error : errores) {
            campos.add(error.getCampo());
        }
        return campos;
    }
}
