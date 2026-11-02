package co.edu.unicauca.bancopreguntas.preguntas.presentacion;

import co.edu.unicauca.bancopreguntas.preguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.Opcion;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.preguntas.presentacion.dto.PreguntaRequest;
import co.edu.unicauca.bancopreguntas.preguntas.presentacion.dto.PreguntaResponse;

import java.util.ArrayList;
import java.util.List;

/** Traduce entre los DTO de la API REST y la entidad de dominio. */
public final class PreguntaMapper {

    private PreguntaMapper() {
    }

    public static Pregunta.Builder aBuilder(PreguntaRequest r) {
        List<String> opciones = r.opciones() == null ? List.of() : r.opciones();
        int indice = r.indiceCorrecta() == null ? -1 : r.indiceCorrecta();
        return new Pregunta.Builder()
                .contexto(r.contexto())
                .enunciado(r.enunciado())
                .opcionesConCorrecta(opciones, indice)
                .justificacion(r.justificacion())
                .bibliografia(r.bibliografia())
                .competencia(r.competencia())
                .tema(r.tema())
                .subtema(r.subtema())
                .nivelDificultad(nivel(r.nivelDificultad()));
    }

    public static PreguntaResponse aResponse(Pregunta p) {
        List<PreguntaResponse.OpcionResponse> opciones = new ArrayList<>();
        for (Opcion opcion : p.getOpciones()) {
            opciones.add(new PreguntaResponse.OpcionResponse(opcion.getTexto(), opcion.esCorrecta()));
        }
        return new PreguntaResponse(
                p.getId(), p.getCodigo(), p.getContexto(), p.getEnunciado(), opciones,
                p.getJustificacion(), p.getBibliografia(), p.getCompetencia(), p.getTema(),
                p.getSubtema(),
                p.getNivelDificultad() == null ? null : p.getNivelDificultad().name(),
                p.getAutorLogin(), p.getFechaCreacion(), p.getFechaEnvioRevision(),
                new PreguntaResponse.EstadoResponse(p.getEstado().getNombre(),
                        p.getEstado().getColorHex(), p.esEditable()),
                p.getRevisores());
    }

    private static NivelDificultad nivel(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return NivelDificultad.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // la regla de validacion de nivel reporta el error
        }
    }
}
