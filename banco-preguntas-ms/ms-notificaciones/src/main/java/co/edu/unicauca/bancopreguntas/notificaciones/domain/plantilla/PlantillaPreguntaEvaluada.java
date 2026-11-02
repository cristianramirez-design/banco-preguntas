package co.edu.unicauca.bancopreguntas.notificaciones.domain.plantilla;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.evento.PreguntaEvaluadaEvento;

import java.util.ArrayList;
import java.util.List;

/** HU-5: avisa del resultado al autor (con las observaciones), al administrador y a los revisores. */
public class PlantillaPreguntaEvaluada extends PlantillaNotificacion<PreguntaEvaluadaEvento> {

    public static final String TIPO = "PREGUNTA_EVALUADA";

    @Override
    protected String tipo() {
        return TIPO;
    }

    @Override
    protected List<Destinatario> destinatarios(PreguntaEvaluadaEvento e) {
        List<Destinatario> lista = new ArrayList<>();
        lista.add(new Destinatario(e.autorLogin(), Papel.AUTOR));
        lista.add(new Destinatario(e.administradorLogin(), Papel.ADMINISTRADOR));
        if (e.observaciones() != null) {
            e.observaciones().forEach(o -> lista.add(new Destinatario(o.revisorLogin(), Papel.REVISOR)));
        }
        return lista;
    }

    @Override
    protected String asunto(PreguntaEvaluadaEvento e, Destinatario d) {
        return "Pregunta " + e.codigo() + (e.aprobada() ? " APROBADA" : " RECHAZADA");
    }

    @Override
    protected String contenido(PreguntaEvaluadaEvento e, Destinatario d) {
        StringBuilder sb = new StringBuilder();
        sb.append("Termino la revision de la pregunta ").append(e.codigo()).append(".\n")
                .append("Resultado: ").append(e.aprobada() ? "APROBADA" : "RECHAZADA").append("\n\n")
                .append("Observaciones de los revisores:\n");
        if (e.observaciones() == null || e.observaciones().isEmpty()) {
            sb.append("  (sin observaciones)\n");
        } else {
            for (PreguntaEvaluadaEvento.Observacion o : e.observaciones()) {
                String texto = o.texto() == null || o.texto().isBlank() ? "(sin comentarios)" : o.texto();
                sb.append("  - ").append(o.revisorLogin()).append(" [").append(o.decision()).append("]: ")
                        .append(texto).append('\n');
            }
        }
        if (d.papel() == Papel.AUTOR && !e.aprobada()) {
            sb.append("\nPuede corregir la pregunta en \"Mis preguntas\" y enviarla de nuevo a revision.");
        }
        return sb.toString();
    }
}
