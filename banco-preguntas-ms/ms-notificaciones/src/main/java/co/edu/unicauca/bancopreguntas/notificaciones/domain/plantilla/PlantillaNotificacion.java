package co.edu.unicauca.bancopreguntas.notificaciones.domain.plantilla;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.model.Mensaje;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Patron Template Method. {@link #generar} fija el algoritmo para armar los correos de un evento:
 * 1) decidir destinatarios, 2) armar asunto y 3) armar contenido para cada uno, 4) envolverlo con
 * saludo y firma comunes. Las subclases solo definen los pasos que cambian.
 */
public abstract class PlantillaNotificacion<E> {

    public static final String FIRMA = "\n\n--\nBanco de Preguntas Saber Pro - Universidad del Cauca\n"
            + "Este es un mensaje automatico, por favor no responda.";

    public final List<Mensaje> generar(E evento) {
        List<Mensaje> mensajes = new ArrayList<>();
        Set<String> yaIncluidos = new LinkedHashSet<>();
        for (Destinatario d : destinatarios(evento)) {
            if (d.login() == null || d.login().isBlank() || !yaIncluidos.add(d.login())) {
                continue;
            }
            String cuerpo = "Hola " + d.login() + ",\n\n" + contenido(evento, d) + FIRMA;
            mensajes.add(new Mensaje(d.login(), asunto(evento, d), cuerpo, tipo()));
        }
        return mensajes;
    }

    protected abstract String tipo();

    protected abstract List<Destinatario> destinatarios(E evento);

    protected abstract String asunto(E evento, Destinatario destinatario);

    protected abstract String contenido(E evento, Destinatario destinatario);

    /** Destinatario con el papel que cumple en el proceso, para adaptar el texto. */
    public record Destinatario(String login, Papel papel) {
    }

    public enum Papel { AUTOR, REVISOR, ADMINISTRADOR }
}
