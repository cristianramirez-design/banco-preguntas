package co.edu.unicauca.bancopreguntas.preguntas.domain.servicio;

import co.edu.unicauca.bancopreguntas.preguntas.domain.busqueda.CriterioBusqueda;
import co.edu.unicauca.bancopreguntas.preguntas.domain.busqueda.IFiltroPregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.busqueda.PaginaResultado;
import co.edu.unicauca.bancopreguntas.preguntas.domain.evento.PreguntaEnviadaARevisionEvento;
import co.edu.unicauca.bancopreguntas.preguntas.domain.excepcion.PreguntaIncompletaException;
import co.edu.unicauca.bancopreguntas.preguntas.domain.excepcion.PreguntaNoEncontradaException;
import co.edu.unicauca.bancopreguntas.preguntas.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.preguntas.domain.excepcion.ValidacionException;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.repositorio.IPreguntaRepository;
import co.edu.unicauca.bancopreguntas.preguntas.domain.validacion.IValidadorPregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.validacion.ResultadoValidacion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Casos de uso de la Gestion de Preguntas. No conoce Spring, JPA ni RabbitMQ:
 * recibe sus dependencias por constructor como interfaces (DIP).
 */
public class PreguntaService implements IPreguntaService {

    private final IPreguntaRepository repositorio;
    private final IValidadorPregunta validador;
    private final IGeneradorCodigo generadorCodigo;
    private final IPublicadorEventos publicador;

    public PreguntaService(IPreguntaRepository repositorio,
                           IValidadorPregunta validador,
                           IGeneradorCodigo generadorCodigo,
                           IPublicadorEventos publicador) {
        this.repositorio = repositorio;
        this.validador = validador;
        this.generadorCodigo = generadorCodigo;
        this.publicador = publicador;
    }

    @Override
    public Pregunta crear(Pregunta pregunta) {
        ResultadoValidacion resultado = validador.validar(pregunta);
        if (!resultado.esValido()) {
            throw new ValidacionException(resultado);
        }
        // La pregunta nueva siempre nace en Borrador, aunque el cliente envie otro estado.
        Pregunta.Builder builder = pregunta.aBuilder().estado(EstadosPregunta.borrador());
        if (pregunta.getCodigo() == null || pregunta.getCodigo().isBlank()) {
            builder.codigo(generadorCodigo.siguiente());
        }
        return repositorio.guardar(builder.construir());
    }

    @Override
    public Pregunta actualizar(Pregunta cambios, String loginSolicitante) {
        Pregunta existente = obtener(cambios.getId());

        if (!existente.perteneceA(loginSolicitante)) {
            throw new ReglaNegocioException("Solo el autor de la pregunta puede editarla.");
        }
        if (!existente.esEditable()) {
            throw new ReglaNegocioException("Solo se pueden editar preguntas en estado "
                    + "Borrador o Rechazada. Estado actual: " + existente.getEstado().getNombre() + ".");
        }

        // Solo se copian los campos editables; codigo, autor, estado y fechas se conservan.
        Pregunta actualizada = existente.aBuilder()
                .contexto(cambios.getContexto())
                .enunciado(cambios.getEnunciado())
                .opciones(cambios.getOpciones())
                .justificacion(cambios.getJustificacion())
                .bibliografia(cambios.getBibliografia())
                .competencia(cambios.getCompetencia())
                .tema(cambios.getTema())
                .subtema(cambios.getSubtema())
                .nivelDificultad(cambios.getNivelDificultad())
                .construir();

        ResultadoValidacion resultado = validador.validar(actualizada);
        if (!resultado.esValido()) {
            throw new ValidacionException(resultado);
        }
        repositorio.actualizar(actualizada);
        return actualizada;
    }

    @Override
    public Pregunta enviarARevision(String idPregunta, String loginSolicitante) {
        Pregunta pregunta = obtener(idPregunta);

        if (!pregunta.perteneceA(loginSolicitante)) {
            throw new ReglaNegocioException("Solo el autor de la pregunta puede enviarla a revision.");
        }

        // HU-2, CA2: la pregunta debe estar completa antes de cambiar de estado.
        ResultadoValidacion resultado = validador.validar(pregunta);
        if (!resultado.esValido()) {
            throw new PreguntaIncompletaException(resultado);
        }

        pregunta.enviarARevision();
        repositorio.actualizar(pregunta);

        publicador.preguntaEnviadaARevision(new PreguntaEnviadaARevisionEvento(
                pregunta.getId(),
                pregunta.getCodigo(),
                pregunta.getEnunciado(),
                pregunta.getTema(),
                pregunta.getCompetencia(),
                pregunta.getAutorLogin(),
                pregunta.getFechaEnvioRevision()));
        return pregunta;
    }

    @Override
    public PaginaResultado<Pregunta> listar(CriterioBusqueda criterio, int pagina, int tamanioPagina) {
        if (pagina < 1) {
            throw new IllegalArgumentException("El numero de pagina inicia en 1.");
        }
        if (tamanioPagina < 1) {
            throw new IllegalArgumentException("El tamanio de pagina debe ser mayor que cero.");
        }

        IFiltroPregunta filtro = criterio.aFiltro();

        List<Pregunta> filtradas = new ArrayList<>();
        for (Pregunta pregunta : repositorio.listarTodas()) {
            if (filtro.cumple(pregunta)) {
                filtradas.add(pregunta);
            }
        }
        filtradas.sort(Comparator.comparing(Pregunta::getFechaCreacion).reversed());

        int total = filtradas.size();
        int desde = Math.min((pagina - 1) * tamanioPagina, total);
        int hasta = Math.min(desde + tamanioPagina, total);

        return new PaginaResultado<>(filtradas.subList(desde, hasta), pagina, tamanioPagina, total);
    }

    @Override
    public Optional<Pregunta> buscarPorId(String idPregunta) {
        return repositorio.buscarPorId(idPregunta);
    }

    @Override
    public void marcarEnRevision(String idPregunta, List<String> revisores) {
        Pregunta pregunta = obtener(idPregunta);
        if (pregunta.getEstado() == EstadosPregunta.enRevision()) {
            return; // evento repetido: la operacion es idempotente
        }
        pregunta.asignarRevisores(revisores);
        repositorio.actualizar(pregunta);
    }

    @Override
    public void registrarResultadoRevision(String idPregunta, boolean aprobada) {
        Pregunta pregunta = obtener(idPregunta);
        if (pregunta.getEstado() == EstadosPregunta.aprobada()
                || pregunta.getEstado() == EstadosPregunta.rechazada()) {
            return; // evento repetido
        }
        if (aprobada) {
            pregunta.aprobar();
        } else {
            pregunta.rechazar();
        }
        repositorio.actualizar(pregunta);
    }

    private Pregunta obtener(String idPregunta) {
        return repositorio.buscarPorId(idPregunta)
                .orElseThrow(() -> new PreguntaNoEncontradaException(idPregunta));
    }
}
