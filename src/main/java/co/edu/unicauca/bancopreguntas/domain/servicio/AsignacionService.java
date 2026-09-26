package co.edu.unicauca.bancopreguntas.domain.servicio;

import co.edu.unicauca.bancopreguntas.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.domain.model.Usuario;
import co.edu.unicauca.bancopreguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.domain.notificacion.EventoAsignacion;
import co.edu.unicauca.bancopreguntas.domain.notificacion.IObservadorAsignacion;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IPreguntaRepository;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IUsuarioRepository;

import java.util.ArrayList;
import java.util.List;

public class AsignacionService implements IAsignacionService {

    private final IPreguntaRepository preguntaRepositorio;
    private final IUsuarioRepository usuarioRepositorio;
    private final List<IObservadorAsignacion> observadores = new ArrayList<>();

    public AsignacionService(IPreguntaRepository preguntaRepositorio,
                             IUsuarioRepository usuarioRepositorio) {
        this.preguntaRepositorio = preguntaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    public void registrarObservador(IObservadorAsignacion observador) {
        observadores.add(observador);
    }

    @Override
    public List<Pregunta> listarPendientesDeRevision() {
        List<Pregunta> pendientes = new ArrayList<>();
        for (Pregunta pregunta : preguntaRepositorio.listarTodas()) {
            if (pregunta.getEstado().getNombre()
                    .equals(EstadosPregunta.pendienteRevision().getNombre())) {
                pendientes.add(pregunta);
            }
        }
        return pendientes;
    }

    @Override
    public List<Usuario> listarRevisoresDisponibles(String idPregunta) {
        Pregunta pregunta = preguntaRepositorio.buscarPorId(idPregunta)
                .orElseThrow(() -> new ReglaNegocioException("La pregunta no existe."));

        List<Usuario> disponibles = new ArrayList<>();
        for (Usuario usuario : usuarioRepositorio.listarPorRol(Rol.REVISOR)) {
            if (!usuario.getLogin().equalsIgnoreCase(pregunta.getAutorLogin())) {
                disponibles.add(usuario);
            }
        }
        return disponibles;
    }

    @Override
    public Pregunta asignarRevisores(String idPregunta,
                                     List<String> loginsRevisores,
                                     String loginAdministrador) {

        if (loginsRevisores == null || loginsRevisores.isEmpty()) {
            throw new ReglaNegocioException("Debe asignar al menos un revisor.");
        }

        Pregunta pregunta = preguntaRepositorio.buscarPorId(idPregunta)
                .orElseThrow(() -> new ReglaNegocioException("La pregunta no existe."));

        Usuario administrador = usuarioRepositorio.buscarPorLogin(loginAdministrador)
                .orElseThrow(() -> new ReglaNegocioException("El usuario no existe."));

        if (!administrador.tieneRol(Rol.ADMINISTRADOR)) {
            throw new ReglaNegocioException("Solo el administrador puede asignar revisores.");
        }

        List<Usuario> revisores = new ArrayList<>();
        for (String login : loginsRevisores) {
            Usuario revisor = usuarioRepositorio.buscarPorLogin(login)
                    .orElseThrow(() -> new ReglaNegocioException(
                            "El revisor '" + login + "' no existe."));
            if (!revisor.tieneRol(Rol.REVISOR)) {
                throw new ReglaNegocioException(
                        "El usuario '" + login + "' no tiene el rol de revisor.");
            }
            if (revisor.getLogin().equalsIgnoreCase(pregunta.getAutorLogin())) {
                throw new ReglaNegocioException(
                        "El autor de la pregunta no puede ser su propio revisor.");
            }
            revisores.add(revisor);
        }

        // La transicion de estado la resuelve el patron State dentro de la entidad.
        pregunta.asignarRevisores(loginsRevisores);
        preguntaRepositorio.actualizar(pregunta);

        EventoAsignacion evento = new EventoAsignacion(pregunta, revisores, loginAdministrador);
        for (IObservadorAsignacion observador : observadores) {
            observador.alAsignarRevisores(evento);
        }
        return pregunta;
    }
}
