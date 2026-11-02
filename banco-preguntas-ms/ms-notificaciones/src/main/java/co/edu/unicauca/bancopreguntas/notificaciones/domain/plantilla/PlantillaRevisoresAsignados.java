package co.edu.unicauca.bancopreguntas.notificaciones.domain.plantilla;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.evento.RevisoresAsignadosEvento;

import java.util.ArrayList;
import java.util.List;

/** HU-4: avisa a cada revisor que tiene una pregunta asignada y al autor que su pregunta entro a revision. */
public class PlantillaRevisoresAsignados extends PlantillaNotificacion<RevisoresAsignadosEvento> {

    public static final String TIPO = "REVISORES_ASIGNADOS";

    @Override
    protected String tipo() {
        return TIPO;
    }

    @Override
    protected List<Destinatario> destinatarios(RevisoresAsignadosEvento e) {
        List<Destinatario> lista = new ArrayList<>();
        e.revisores().forEach(r -> lista.add(new Destinatario(r, Papel.REVISOR)));
        lista.add(new Destinatario(e.autorLogin(), Papel.AUTOR));
        return lista;
    }

    @Override
    protected String asunto(RevisoresAsignadosEvento e, Destinatario d) {
        return d.papel() == Papel.REVISOR
                ? "Nueva pregunta asignada para revision: " + e.codigo()
                : "Su pregunta " + e.codigo() + " ya tiene revisores";
    }

    @Override
    protected String contenido(RevisoresAsignadosEvento e, Destinatario d) {
        String datos = "Codigo: " + e.codigo() + "\nCompetencia: " + e.competencia() + "\nTema: " + e.tema()
                + "\nEnunciado: " + e.enunciado();
        if (d.papel() == Papel.REVISOR) {
            return "El administrador le asigno la siguiente pregunta para revisar:\n\n" + datos
                    + "\n\nIngrese al sistema, opcion \"Mis revisiones\", para aprobarla o rechazarla.";
        }
        return "Su pregunta fue asignada a " + e.revisores().size() + " revisor(es):\n\n" + datos
                + "\n\nLe avisaremos cuando terminen la revision.";
    }
}
