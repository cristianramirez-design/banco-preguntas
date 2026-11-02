package co.edu.unicauca.bancopreguntas.revision.acceso.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "revisiones", indexes = {
        @Index(name = "idx_revision_revisor", columnList = "revisorLogin"),
        @Index(name = "idx_revision_pregunta", columnList = "preguntaId")})
public class RevisionJpaEntity {

    @Id
    private String id;
    private String preguntaId;
    private String codigoPregunta;
    @Column(length = 2000)
    private String enunciado;
    private int ronda;
    private String revisorLogin;
    private LocalDateTime fechaAsignacion;
    private String estado;
    @Column(length = 2000)
    private String observaciones;
    private LocalDateTime fechaEvaluacion;

    protected RevisionJpaEntity() {
    }

    public RevisionJpaEntity(String id, String preguntaId, String codigoPregunta, String enunciado, int ronda,
                             String revisorLogin, LocalDateTime fechaAsignacion, String estado,
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

    public String getId() { return id; }
    public String getPreguntaId() { return preguntaId; }
    public String getCodigoPregunta() { return codigoPregunta; }
    public String getEnunciado() { return enunciado; }
    public int getRonda() { return ronda; }
    public String getRevisorLogin() { return revisorLogin; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public String getEstado() { return estado; }
    public String getObservaciones() { return observaciones; }
    public LocalDateTime getFechaEvaluacion() { return fechaEvaluacion; }
}
