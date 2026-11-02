package co.edu.unicauca.bancopreguntas.revision.domain.servicio;

import co.edu.unicauca.bancopreguntas.revision.domain.evento.PreguntaEnviadaARevisionEvento;
import co.edu.unicauca.bancopreguntas.revision.domain.evento.RevisoresAsignadosEvento;
import co.edu.unicauca.bancopreguntas.revision.domain.excepcion.RecursoNoEncontradoException;
import co.edu.unicauca.bancopreguntas.revision.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.revision.domain.model.PreguntaPendiente;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Revision;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IDirectorioUsuarios;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IPreguntaPendienteRepositorio;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IPublicadorEventos;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IRevisionRepositorio;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.UsuarioInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** HU-4: bandeja de preguntas pendientes y asignacion de 1 a 3 revisores. */
public class AsignacionService {

    public static final int MINIMO_REVISORES = 1;
    public static final int MAXIMO_REVISORES = 3;

    public static final String MENSAJE_SIN_REVISORES = "Debe seleccionar al menos un revisor";
    public static final String MENSAJE_MAXIMO_REVISORES =
            "Puede asignar máximo " + MAXIMO_REVISORES + " revisores";

    private final IPreguntaPendienteRepositorio pendientes;
    private final IRevisionRepositorio revisiones;
    private final IDirectorioUsuarios directorio;
    private final IPublicadorEventos publicador;

    public AsignacionService(IPreguntaPendienteRepositorio pendientes,
                             IRevisionRepositorio revisiones,
                             IDirectorioUsuarios directorio,
                             IPublicadorEventos publicador) {
        this.pendientes = pendientes;
        this.revisiones = revisiones;
        this.directorio = directorio;
        this.publicador = publicador;
    }

    /** Reaccion al evento PreguntaEnviadaARevision publicado por ms-preguntas. */
    public PreguntaPendiente registrarPendiente(PreguntaEnviadaARevisionEvento e) {
        PreguntaPendiente pendiente = pendientes.buscarPorId(e.preguntaId())
                .map(existente -> {
                    existente.reenviar(e.codigo(), e.enunciado(), e.tema(), e.competencia(), e.fechaEnvio());
                    return existente;
                })
                .orElseGet(() -> PreguntaPendiente.nueva(e.preguntaId(), e.codigo(), e.enunciado(),
                        e.tema(), e.competencia(), e.autorLogin(), e.fechaEnvio()));
        return pendientes.guardar(pendiente);
    }

    /** HU-4, CA3: preguntas en Pendiente de revision que aun no tienen revisores. */
    public List<PreguntaPendiente> listarPendientes() {
        return pendientes.listarSinAsignar().stream()
                .sorted(Comparator.comparing(PreguntaPendiente::getFechaEnvio))
                .toList();
    }

    /** HU-4, CA1 y CA2: asigna entre 1 y 3 revisores y publica RevisoresAsignados. */
    public List<Revision> asignar(String preguntaId, List<String> loginsRevisores, String administradorLogin) {
        UsuarioInfo administrador = directorio.buscar(administradorLogin)
                .orElseThrow(() -> new ReglaNegocioException("No existe el usuario " + administradorLogin + "."));
        if (!administrador.esAdministrador()) {
            throw new ReglaNegocioException("Solo el administrador puede asignar revisores.");
        }

        Set<String> logins = new LinkedHashSet<>();
        if (loginsRevisores != null) {
            for (String login : loginsRevisores) {
                if (login != null && !login.isBlank()) {
                    logins.add(login.trim().toLowerCase());
                }
            }
        }
        if (logins.size() < MINIMO_REVISORES) {
            throw new ReglaNegocioException(MENSAJE_SIN_REVISORES);
        }
        if (logins.size() > MAXIMO_REVISORES) {
            throw new ReglaNegocioException(MENSAJE_MAXIMO_REVISORES);
        }

        PreguntaPendiente pendiente = pendientes.buscarPorId(preguntaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "La pregunta no esta pendiente de revision: " + preguntaId));
        if (pendiente.isAsignada()) {
            throw new ReglaNegocioException("La pregunta " + pendiente.getCodigo() + " ya tiene revisores asignados.");
        }

        for (String login : logins) {
            UsuarioInfo revisor = directorio.buscar(login)
                    .orElseThrow(() -> new ReglaNegocioException("No existe el usuario " + login + "."));
            if (!revisor.esRevisor()) {
                throw new ReglaNegocioException(revisor.nombre() + " no tiene el rol de revisor.");
            }
            if (login.equalsIgnoreCase(pendiente.getAutorLogin())) {
                throw new ReglaNegocioException("El autor no puede revisar su propia pregunta.");
            }
        }

        List<Revision> creadas = new ArrayList<>();
        for (String login : logins) {
            creadas.add(revisiones.guardar(Revision.asignar(pendiente, login)));
        }
        pendiente.marcarAsignada(administrador.login());
        pendientes.guardar(pendiente);

        publicador.revisoresAsignados(new RevisoresAsignadosEvento(
                pendiente.getPreguntaId(), pendiente.getCodigo(), pendiente.getEnunciado(),
                pendiente.getTema(), pendiente.getCompetencia(), pendiente.getAutorLogin(),
                List.copyOf(logins), administrador.login(), LocalDateTime.now()));
        return creadas;
    }
}
