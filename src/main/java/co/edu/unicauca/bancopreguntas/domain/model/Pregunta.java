package co.edu.unicauca.bancopreguntas.domain.model;

import co.edu.unicauca.bancopreguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.domain.model.estado.IEstadoPregunta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad central del dominio. Representa una pregunta de seleccion multiple
 * con unica respuesta construida bajo el Diseno Centrado en Evidencia (HU-1).
 *
 * Se construye con el patron GoF Builder porque tiene mas de diez atributos
 * obligatorios y un constructor telescopico seria inmanejable.
 */
public class Pregunta {

    private final String id;
    private final String codigo;
    private final String contexto;
    private final String enunciado;
    private final List<Opcion> opciones;
    private final String justificacion;
    private final String bibliografia;
    private final String competencia;
    private final String tema;
    private final String subtema;
    private final NivelDificultad nivelDificultad;
    private final String autorLogin;
    private final LocalDateTime fechaCreacion;

    private IEstadoPregunta estado;
    private LocalDateTime fechaEnvioRevision;
    private final List<String> revisores;

    private Pregunta(Builder builder) {
        this.id = builder.id;
        this.codigo = builder.codigo;
        this.contexto = builder.contexto;
        this.enunciado = builder.enunciado;
        this.opciones = new ArrayList<>(builder.opciones);
        this.justificacion = builder.justificacion;
        this.bibliografia = builder.bibliografia;
        this.competencia = builder.competencia;
        this.tema = builder.tema;
        this.subtema = builder.subtema;
        this.nivelDificultad = builder.nivelDificultad;
        this.autorLogin = builder.autorLogin;
        this.fechaCreacion = builder.fechaCreacion;
        this.estado = builder.estado;
        this.fechaEnvioRevision = builder.fechaEnvioRevision;
        this.revisores = new ArrayList<>(builder.revisores);
    }

    // ------------------------------------------------------------------
    // Comportamiento de negocio (transiciones delegadas al patron State)
    // ------------------------------------------------------------------

    /** HU-2: el autor envia su pregunta a revision. */
    public void enviarARevision() {
        this.estado = this.estado.enviarARevision();
        this.fechaEnvioRevision = LocalDateTime.now();
    }

    /** HU-4: el administrador asigna revisores y la pregunta pasa a En revision. */
    public void asignarRevisores(List<String> loginsRevisores) {
        this.estado = this.estado.asignarRevisores();
        this.revisores.clear();
        this.revisores.addAll(loginsRevisores);
    }

    public void aprobar() {
        this.estado = this.estado.aprobar();
    }

    public void rechazar() {
        this.estado = this.estado.rechazar();
    }

    public boolean perteneceA(String login) {
        return autorLogin != null && autorLogin.equalsIgnoreCase(login);
    }

    public boolean esEditable() {
        return estado.esEditable();
    }

    /** La opcion marcada como correcta, o null si no se ha marcado ninguna. */
    public Opcion getRespuestaCorrecta() {
        for (Opcion opcion : opciones) {
            if (opcion.esCorrecta()) {
                return opcion;
            }
        }
        return null;
    }

    /** Las opciones que no son la respuesta correcta. */
    public List<Opcion> getDistractores() {
        List<Opcion> distractores = new ArrayList<>();
        for (Opcion opcion : opciones) {
            if (!opcion.esCorrecta()) {
                distractores.add(opcion);
            }
        }
        return List.copyOf(distractores);
    }

    /** Indice (base 0) de la opcion correcta, o -1 si no hay ninguna marcada. */
    public int getIndiceRespuestaCorrecta() {
        for (int i = 0; i < opciones.size(); i++) {
            if (opciones.get(i).esCorrecta()) {
                return i;
            }
        }
        return -1;
    }

    // ------------------------------------------------------------------
    // Accesores
    // ------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getContexto() {
        return contexto;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public List<Opcion> getOpciones() {
        return List.copyOf(opciones);
    }

    public String getJustificacion() {
        return justificacion;
    }

    public String getBibliografia() {
        return bibliografia;
    }

    public String getCompetencia() {
        return competencia;
    }

    public String getTema() {
        return tema;
    }

    public String getSubtema() {
        return subtema;
    }

    public NivelDificultad getNivelDificultad() {
        return nivelDificultad;
    }

    public String getAutorLogin() {
        return autorLogin;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaEnvioRevision() {
        return fechaEnvioRevision;
    }

    public IEstadoPregunta getEstado() {
        return estado;
    }

    public List<String> getRevisores() {
        return List.copyOf(revisores);
    }

    /** Builder precargado con los datos de esta pregunta, para editarla (HU-3, CA4). */
    public Builder aBuilder() {
        return new Builder()
                .id(id)
                .codigo(codigo)
                .contexto(contexto)
                .enunciado(enunciado)
                .opciones(opciones)
                .justificacion(justificacion)
                .bibliografia(bibliografia)
                .competencia(competencia)
                .tema(tema)
                .subtema(subtema)
                .nivelDificultad(nivelDificultad)
                .autorLogin(autorLogin)
                .fechaCreacion(fechaCreacion)
                .estado(estado)
                .fechaEnvioRevision(fechaEnvioRevision)
                .revisores(revisores);
    }

    @Override
    public String toString() {
        return codigo + " - " + enunciado;
    }

    // ------------------------------------------------------------------
    // Patron GoF Builder
    // ------------------------------------------------------------------

    public static class Builder {

        private String id = UUID.randomUUID().toString();
        private String codigo = "";
        private String contexto = "";
        private String enunciado = "";
        private List<Opcion> opciones = new ArrayList<>();
        private String justificacion = "";
        private String bibliografia = "";
        private String competencia = "";
        private String tema = "";
        private String subtema = "";
        private NivelDificultad nivelDificultad;
        private String autorLogin = "";
        private LocalDateTime fechaCreacion = LocalDateTime.now();
        private IEstadoPregunta estado = EstadosPregunta.borrador();
        private LocalDateTime fechaEnvioRevision;
        private List<String> revisores = new ArrayList<>();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder codigo(String codigo) {
            this.codigo = texto(codigo);
            return this;
        }

        public Builder contexto(String contexto) {
            this.contexto = texto(contexto);
            return this;
        }

        public Builder enunciado(String enunciado) {
            this.enunciado = texto(enunciado);
            return this;
        }

        public Builder opciones(List<Opcion> opciones) {
            this.opciones = new ArrayList<>(opciones);
            return this;
        }

        public Builder opcion(String textoOpcion, boolean correcta) {
            this.opciones.add(Opcion.nueva(textoOpcion, correcta));
            return this;
        }

        public Builder opcionCorrecta(String textoOpcion) {
            return opcion(textoOpcion, true);
        }

        public Builder opcionDistractor(String textoOpcion) {
            return opcion(textoOpcion, false);
        }

        /** Carga los textos y marca cual de ellos es la respuesta correcta. */
        public Builder opcionesConCorrecta(List<String> textos, int indiceCorrecta) {
            this.opciones = new ArrayList<>();
            for (int i = 0; i < textos.size(); i++) {
                this.opciones.add(Opcion.nueva(textos.get(i), i == indiceCorrecta));
            }
            return this;
        }

        public Builder justificacion(String justificacion) {
            this.justificacion = texto(justificacion);
            return this;
        }

        public Builder bibliografia(String bibliografia) {
            this.bibliografia = texto(bibliografia);
            return this;
        }

        public Builder competencia(String competencia) {
            this.competencia = texto(competencia);
            return this;
        }

        public Builder tema(String tema) {
            this.tema = texto(tema);
            return this;
        }

        public Builder subtema(String subtema) {
            this.subtema = texto(subtema);
            return this;
        }

        public Builder nivelDificultad(NivelDificultad nivel) {
            this.nivelDificultad = nivel;
            return this;
        }

        public Builder autorLogin(String autorLogin) {
            this.autorLogin = texto(autorLogin);
            return this;
        }

        public Builder fechaCreacion(LocalDateTime fecha) {
            this.fechaCreacion = fecha;
            return this;
        }

        public Builder fechaEnvioRevision(LocalDateTime fecha) {
            this.fechaEnvioRevision = fecha;
            return this;
        }

        public Builder estado(IEstadoPregunta estado) {
            this.estado = estado;
            return this;
        }

        public Builder revisores(List<String> revisores) {
            this.revisores = new ArrayList<>(revisores);
            return this;
        }

        /**
         * Construye la pregunta. No valida reglas de negocio a proposito:
         * de eso se encarga el validador estructural (principio SRP).
         */
        public Pregunta construir() {
            return new Pregunta(this);
        }

        private static String texto(String valor) {
            return valor == null ? "" : valor.trim();
        }
    }
}
