package co.edu.unicauca.bancopreguntas.domain.servicio;

import co.edu.unicauca.bancopreguntas.domain.busqueda.CriterioBusqueda;
import co.edu.unicauca.bancopreguntas.domain.busqueda.IFiltroPregunta;
import co.edu.unicauca.bancopreguntas.domain.busqueda.PaginaResultado;
import co.edu.unicauca.bancopreguntas.domain.excepcion.PreguntaIncompletaException;
import co.edu.unicauca.bancopreguntas.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.domain.excepcion.ValidacionException;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IPreguntaRepository;
import co.edu.unicauca.bancopreguntas.domain.validacion.IValidadorPregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.ResultadoValidacion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PreguntaService implements IPreguntaService {

    private final IPreguntaRepository repositorio;
    private final IValidadorPregunta validador;
    private final IGeneradorCodigo generadorCodigo;

    /** Inyeccion por constructor: el servicio depende de abstracciones, no de clases concretas. */
    public PreguntaService(IPreguntaRepository repositorio,
                           IValidadorPregunta validador,
                           IGeneradorCodigo generadorCodigo) {
        this.repositorio = repositorio;
        this.validador = validador;
        this.generadorCodigo = generadorCodigo;
    }

    @Override
    public Pregunta crear(Pregunta pregunta) {
        ResultadoValidacion resultado = validador.validar(pregunta);
        if (!resultado.esValido()) {
            throw new ValidacionException(resultado);
        }

        Pregunta aGuardar = pregunta;
        if (pregunta.getCodigo() == null || pregunta.getCodigo().isBlank()) {
            aGuardar = pregunta.aBuilder().codigo(generadorCodigo.siguiente()).construir();
        }
        return repositorio.guardar(aGuardar);
    }

    @Override
    public Pregunta actualizar(Pregunta pregunta, String loginSolicitante) {
        Pregunta existente = repositorio.buscarPorId(pregunta.getId())
                .orElseThrow(() -> new ReglaNegocioException("La pregunta no existe."));

        if (!existente.perteneceA(loginSolicitante)) {
            throw new ReglaNegocioException("Solo el autor de la pregunta puede editarla.");
        }
        if (!existente.esEditable()) {
            throw new ReglaNegocioException("Solo se pueden editar preguntas en estado "
                    + "Borrador o Rechazada. Estado actual: " + existente.getEstado().getNombre() + ".");
        }

        ResultadoValidacion resultado = validador.validar(pregunta);
        if (!resultado.esValido()) {
            throw new ValidacionException(resultado);
        }

        repositorio.actualizar(pregunta);
        return pregunta;
    }

    @Override
    public Pregunta enviarARevision(String idPregunta, String loginSolicitante) {
        Pregunta pregunta = repositorio.buscarPorId(idPregunta)
                .orElseThrow(() -> new ReglaNegocioException("La pregunta no existe."));

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
}
