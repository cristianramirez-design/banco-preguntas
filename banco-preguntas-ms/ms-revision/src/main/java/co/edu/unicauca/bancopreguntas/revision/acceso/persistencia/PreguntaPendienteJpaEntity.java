package co.edu.unicauca.bancopreguntas.revision.acceso.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "preguntas_pendientes")
public class PreguntaPendienteJpaEntity {

    @Id
    private String preguntaId;
    private String codigo;
    @Column(length = 2000)
    private String enunciado;
    private String tema;
    private String competencia;
    private String autorLogin;
    private LocalDateTime fechaEnvio;
    private int ronda;
    private boolean asignada;
    private String administradorLogin;
    private LocalDateTime fechaAsignacion;

    protected PreguntaPendienteJpaEntity() {
    }

    public PreguntaPendienteJpaEntity(String preguntaId, String codigo, String enunciado, String tema,
                                      String competencia, String autorLogin, LocalDateTime fechaEnvio, int ronda,
                                      boolean asignada, String administradorLogin, LocalDateTime fechaAsignacion) {
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
