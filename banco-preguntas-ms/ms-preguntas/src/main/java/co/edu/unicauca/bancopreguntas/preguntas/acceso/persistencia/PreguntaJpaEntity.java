package co.edu.unicauca.bancopreguntas.preguntas.acceso.persistencia;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de persistencia (tabla preguntas). Es distinto de la entidad de dominio Pregunta:
 * el adaptador PreguntaRepositorioJpaAdapter traduce entre ambos.
 */
@Entity
@Table(name = "preguntas")
public class PreguntaJpaEntity {

    @Id
    private String id;

    @Column(unique = true)
    private String codigo;

    @Column(length = 4000)
    private String contexto;

    @Column(length = 2000)
    private String enunciado;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pregunta_opciones", joinColumns = @JoinColumn(name = "pregunta_id"))
    @OrderColumn(name = "orden")
    private List<OpcionEmbeddable> opciones = new ArrayList<>();

    @Column(length = 4000)
    private String justificacion;

    @Column(length = 1000)
    private String bibliografia;

    private String competencia;
    private String tema;
    private String subtema;
    private String nivelDificultad;
    private String autorLogin;
    private LocalDateTime fechaCreacion;
    private String estado;
    private LocalDateTime fechaEnvioRevision;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pregunta_revisores", joinColumns = @JoinColumn(name = "pregunta_id"))
    @OrderColumn(name = "orden")
    @Column(name = "revisor_login")
    private List<String> revisores = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getContexto() { return contexto; }
    public void setContexto(String contexto) { this.contexto = contexto; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public List<OpcionEmbeddable> getOpciones() { return opciones; }
    public void setOpciones(List<OpcionEmbeddable> opciones) { this.opciones = opciones; }
    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) { this.justificacion = justificacion; }
    public String getBibliografia() { return bibliografia; }
    public void setBibliografia(String bibliografia) { this.bibliografia = bibliografia; }
    public String getCompetencia() { return competencia; }
    public void setCompetencia(String competencia) { this.competencia = competencia; }
    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }
    public String getSubtema() { return subtema; }
    public void setSubtema(String subtema) { this.subtema = subtema; }
    public String getNivelDificultad() { return nivelDificultad; }
    public void setNivelDificultad(String nivelDificultad) { this.nivelDificultad = nivelDificultad; }
    public String getAutorLogin() { return autorLogin; }
    public void setAutorLogin(String autorLogin) { this.autorLogin = autorLogin; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaEnvioRevision() { return fechaEnvioRevision; }
    public void setFechaEnvioRevision(LocalDateTime fechaEnvioRevision) { this.fechaEnvioRevision = fechaEnvioRevision; }
    public List<String> getRevisores() { return revisores; }
    public void setRevisores(List<String> revisores) { this.revisores = revisores; }
}
