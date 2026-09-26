package co.edu.unicauca.bancopreguntas.domain.notificacion;

import co.edu.unicauca.bancopreguntas.domain.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Observador que deja traza de las asignaciones; util para la sustentacion. */
public class NotificadorBitacora implements IObservadorAsignacion {

    private final List<String> registros = new ArrayList<>();

    @Override
    public void alAsignarRevisores(EventoAsignacion evento) {
        String linea = "[" + evento.getMomento() + "] "
                + evento.getAdministradorLogin() + " asigno la pregunta '"
                + evento.getPregunta().getEnunciado() + "' a "
                + evento.getRevisores().stream().map(Usuario::getLogin).collect(Collectors.joining(", "));
        registros.add(linea);
        System.out.println(linea);
    }

    public List<String> getRegistros() {
        return List.copyOf(registros);
    }
}
