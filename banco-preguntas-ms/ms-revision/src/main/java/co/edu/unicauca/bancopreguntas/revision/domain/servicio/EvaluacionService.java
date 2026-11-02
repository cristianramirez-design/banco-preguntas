package co.edu.unicauca.bancopreguntas.revision.domain.servicio;

import co.edu.unicauca.bancopreguntas.revision.domain.evento.PreguntaEvaluadaEvento;
import co.edu.unicauca.bancopreguntas.revision.domain.excepcion.RecursoNoEncontradoException;
import co.edu.unicauca.bancopreguntas.revision.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Decision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.PreguntaPendiente;
import co.edu.unicauca.bancopreguntas.revision.domain.model.ResultadoRevision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Revision;
import co.edu.unicauca.bancopreguntas.revision.domain.politica.IPoliticaDecision;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IPreguntaPendienteRepositorio;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IPublicadorEventos;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IRevisionRepositorio;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** HU-5: el revisor lista sus preguntas asignadas, deja observaciones y aprueba o rechaza. */
public class EvaluacionService {

    private final IRevisionRepositorio revisiones;
    private final IPreguntaPendienteRepositorio pendientes;
    private final IPoliticaDecision politica;
    private final IPublicadorEventos publicador;

    public EvaluacionService(IRevisionRepositorio revisiones,
                             IPreguntaPendienteRepositorio pendientes,
                             IPoliticaDecision politica,
                             IPublicadorEventos publicador) {
        this.revisiones = revisiones;
        this.pendientes = pendientes;
        this.politica = politica;
        this.publicador = publicador;
    }

    /** HU-5: preguntas asignadas al revisor, las pendientes primero. */
    public List<Revision> listarAsignadas(String revisorLogin) {
        return revisiones.listarPorRevisor(revisorLogin.trim().toLowerCase()).stream()
                .sorted(Comparator.comparing(Revision::estaEvaluada)
                        .thenComparing(Revision::getFechaAsignacion, Comparator.reverseOrder()))
                .toList();
    }

    /**
     * HU-5: registra la evaluacion. Cuando todos los revisores de la ronda evaluaron, la politica
     * (Strategy) decide el resultado y se publica PreguntaEvaluada.
     */
    public Revision evaluar(String revisionId, String revisorLogin, Decision decision, String observaciones) {
        Revision revision = revisiones.buscarPorId(revisionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la revision " + revisionId));
        if (!revision.perteneceA(revisorLogin)) {
            throw new ReglaNegocioException("Solo el revisor asignado puede evaluar esta pregunta.");
        }
        revision.evaluar(decision, observaciones);
        revisiones.guardar(revision);

        List<Revision> deLaRonda = revisiones.listarPorPreguntaYRonda(revision.getPreguntaId(), revision.getRonda());
        Optional<ResultadoRevision> resultado = politica.decidir(deLaRonda);
        resultado.ifPresent(r -> publicarResultado(revision, deLaRonda, r));
        return revision;
    }

    /** Observaciones de todas las rondas, para que el autor las lea. */
    public List<Revision> observacionesDePregunta(String preguntaId) {
        return revisiones.listarPorPregunta(preguntaId).stream()
                .filter(Revision::estaEvaluada)
                .sorted(Comparator.comparing(Revision::getFechaEvaluacion))
                .toList();
    }

    private void publicarResultado(Revision revision, List<Revision> deLaRonda, ResultadoRevision resultado) {
        PreguntaPendiente pendiente = pendientes.buscarPorId(revision.getPreguntaId()).orElse(null);
        List<PreguntaEvaluadaEvento.Observacion> observaciones = deLaRonda.stream()
                .map(r -> new PreguntaEvaluadaEvento.Observacion(r.getRevisorLogin(),
                        r.getEstado().name(), r.getObservaciones()))
                .toList();
        publicador.preguntaEvaluada(new PreguntaEvaluadaEvento(
                revision.getPreguntaId(),
                revision.getCodigoPregunta(),
                revision.getEnunciado(),
                pendiente == null ? null : pendiente.getAutorLogin(),
                pendiente == null ? null : pendiente.getAdministradorLogin(),
                resultado.name(),
                observaciones,
                LocalDateTime.now()));
    }
}
