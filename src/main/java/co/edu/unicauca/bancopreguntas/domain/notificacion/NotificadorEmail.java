package co.edu.unicauca.bancopreguntas.domain.notificacion;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.Usuario;

import java.time.format.DateTimeFormatter;

/** Observador que envia el correo de notificacion a cada revisor asignado. */
public class NotificadorEmail implements IObservadorAsignacion {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final IServicioEmail servicioEmail;

    public NotificadorEmail(IServicioEmail servicioEmail) {
        this.servicioEmail = servicioEmail;
    }

    @Override
    public void alAsignarRevisores(EventoAsignacion evento) {
        for (Usuario revisor : evento.getRevisores()) {
            servicioEmail.enviar(
                    revisor.getEmail(),
                    "Banco de Preguntas Saber Pro: pregunta " + evento.getPregunta().getCodigo() + " asignada para revisión",
                    construirCuerpo(revisor, evento));
        }
    }

    private String construirCuerpo(Usuario revisor, EventoAsignacion evento) {
        Pregunta pregunta = evento.getPregunta();
        return "Estimado(a) " + revisor.getNombre() + ",\n\n"
                + "Se le ha asignado la revisión de la siguiente pregunta:\n\n"
                + "Código: " + pregunta.getCodigo() + "\n"
                + "Pregunta: " + pregunta.getEnunciado() + "\n"
                + "Competencia: " + pregunta.getCompetencia() + "\n"
                + "Tema: " + pregunta.getTema() + " / " + pregunta.getSubtema() + "\n"
                + "Autor: " + pregunta.getAutorLogin() + "\n\n"
                + "Asignada por: " + evento.getAdministradorLogin() + "\n"
                + "Fecha: " + evento.getMomento().format(FORMATO_FECHA) + "\n\n"
                + "Por favor ingrese al Banco de Preguntas Saber Pro para realizar la revisión.\n";
    }
}
