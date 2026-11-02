package co.edu.unicauca.bancopreguntas.revision.domain.model;

import co.edu.unicauca.bancopreguntas.revision.domain.excepcion.ReglaNegocioException;

import java.time.LocalDateTime;

/**
 * Copia local (modelo de lectura) de una pregunta enviada a revision. ms-revision la recibe
 * por el evento PreguntaEnviadaARevision y no consulta la base de datos de ms-preguntas.
 * La ronda aumenta cada vez que el autor corrige una pregunta rechazada y la reenvia.
 */
public class PreguntaPendiente {

    private final String preguntaId;
    private String codigo;
    private String enunciado;
    private String tema;
    private String competencia;
    private String autorLogin;
    private LocalDateTime fechaEnvio;
    private int ronda;
    private boolean asignada;
    private String administradorLogin;
    private LocalDateTime fechaAsignacion;

    public PreguntaPendiente(String preguntaId, String codigo, String enunciado, String tema,
                             String competencia, String autorLogin, LocalDateTime fechaEnvio,
                             int ronda, boolean asignada, String administradorLogin,
                             LocalDateTime fechaAsignacion) {
        this.preguntaId = preguntaId;
        this.codigo = codigo;
        this.enunciado = enunciado;
        this.tema = tema;
        this.competencia = competencia;
        this.autorLogin = autorLogin;
        this.fechaEnvio = fechaEnvio;
        this.ronda = ronda;
        this.asignada = asignada;
        this.administradorLogin = administradorLogin;
        this.fechaAsignacion = fechaAsignacion;
    }

    /** Primera vez que la pregunta llega a revision. */
    public static PreguntaPendiente nueva(String preguntaId, String codigo, String enunciado, String tema,
                                          String competencia, String autorLogin, LocalDateTime fechaEnvio) {
        return new PreguntaPendiente(preguntaId, codigo, enunciado, tema, competencia, autorLogin,
                fechaEnvio == null ? LocalDateTime.now() : fechaEnvio, 1, false, null, null);
    }

    /** La pregunta fue corregida y reenviada: empieza una ronda nueva sin revisores. */
    public void reenviar(String codigo, String enunciado, String tema, String competencia,
                         LocalDateTime fechaEnvio) {
        if (!asignada) {
            return; // evento repetido: ya esta esperando asignacion
        }
        this.codigo = codigo;
        this.enunciado = enunciado;
        this.tema = tema;
        this.competencia = competencia;
        this.fechaEnvio = fechaEnvio == null ? LocalDateTime.now() : fechaEnvio;
        this.ronda++;
        this.asignada = false;
        this.administradorLogin = null;
        this.fechaAsignacion = null;
    }

    public void marcarAsignada(String administradorLogin) {
        if (asignada) {
            throw new ReglaNegocioException(
                    "La pregunta " + codigo + " ya tiene revisores asignados.");
        }
        this.asignada = true;
        this.administradorLogin = administradorLogin;
        this.fechaAsignacion = LocalDateTime.now();
    }

    public String getPreguntaId() { return preguntaId; }
    public String getCodigo() { return codigo; }
    public String getEnunciado() { return enunciado; }
    public String getTema() { return tema; }
    public String getCompetencia() { return competencia; }
    public String getAutorLogin() { return autorLogin; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public int getRonda() { return ronda; }
    public boolean isAsignada() { return asignada; }
    public String getAdministradorLogin() { return administradorLogin; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
}
