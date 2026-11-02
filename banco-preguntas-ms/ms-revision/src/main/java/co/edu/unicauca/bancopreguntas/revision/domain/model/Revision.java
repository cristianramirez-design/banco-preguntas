package co.edu.unicauca.bancopreguntas.revision.domain.model;

import co.edu.unicauca.bancopreguntas.revision.domain.excepcion.ReglaNegocioException;

import java.time.LocalDateTime;
import java.util.UUID;

/** Revision que UN revisor hace de una pregunta en una ronda (HU-5). */
public class Revision {

    public static final String MENSAJE_OBSERVACIONES_OBLIGATORIAS =
            "Debe escribir observaciones para rechazar la pregunta";

    private final String id;
    private final String preguntaId;
    private final String codigoPregunta;
    private final String enunciado;
    private final int ronda;
    private final String revisorLogin;
    private final LocalDateTime fechaAsignacion;
    private EstadoRevision estado;
    private String observaciones;
    private LocalDateTime fechaEvaluacion;

    public Revision(String id, String preguntaId, String codigoPregunta, String enunciado, int ronda,
                    String revisorLogin, LocalDateTime fechaAsignacion, EstadoRevision estado,
                    String observaciones, LocalDateTime fechaEvaluacion) {
        this.id = id;
        this.preguntaId = preguntaId;
        this.codigoPregunta = codigoPregunta;
        this.enunciado = enunciado;
        this.ronda = ronda;
        this.revisorLogin = revisorLogin;
        this.fechaAsignacion = fechaAsignacion;
        this.estado = estado;
        this.observaciones = observaciones;
        this.fechaEvaluacion = fechaEvaluacion;
    }

    /** HU-4: crea la revision cuando el administrador asigna al revisor. */
    public static Revision asignar(PreguntaPendiente pregunta, String revisorLogin) {
        return new Revision(UUID.randomUUID().toString(), pregunta.getPreguntaId(), pregunta.getCodigo(),
                pregunta.getEnunciado(), pregunta.getRonda(), revisorLogin, LocalDateTime.now(),
                EstadoRevision.ASIGNADA, null, null);
    }

    /** HU-5: el revisor deja su evaluacion. Rechazar exige observaciones. */
    public void evaluar(Decision decision, String observaciones) {
        if (estado != EstadoRevision.ASIGNADA) {
            throw new ReglaNegocioException("Esta revision ya fue evaluada (" + estado + ").");
        }
        if (decision == null) {
            throw new ReglaNegocioException("Debe elegir APROBAR o RECHAZAR.");
        }
        String texto = observaciones == null ? "" : observaciones.trim();
        if (decision == Decision.RECHAZAR && texto.isEmpty()) {
            throw new ReglaNegocioException(MENSAJE_OBSERVACIONES_OBLIGATORIAS);
        }
        this.estado = decision == Decision.APROBAR ? EstadoRevision.APROBADA : EstadoRevision.RECHAZADA;
        this.observaciones = texto;
        this.fechaEvaluacion = LocalDateTime.now();
    }

    public boolean estaEvaluada() {
        return estado != EstadoRevision.ASIGNADA;
    }

    public boolean perteneceA(String login) {
        return revisorLogin != null && revisorLogin.equalsIgnoreCase(login);
    }

    public String getId() { return id; }
    public String getPreguntaId() { return preguntaId; }
    public String getCodigoPregunta() { return codigoPregunta; }
    public String getEnunciado() { return enunciado; }
    public int getRonda() { return ronda; }
    public String getRevisorLogin() { return revisorLogin; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public EstadoRevision getEstado() { return estado; }
    public String getObservaciones() { return observaciones; }
    public LocalDateTime getFechaEvaluacion() { return fechaEvaluacion; }
}
