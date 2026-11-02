package co.edu.unicauca.bancopreguntas.revision.util;

import co.edu.unicauca.bancopreguntas.revision.domain.evento.PreguntaEnviadaARevisionEvento;
import co.edu.unicauca.bancopreguntas.revision.domain.evento.PreguntaEvaluadaEvento;
import co.edu.unicauca.bancopreguntas.revision.domain.evento.RevisoresAsignadosEvento;
import co.edu.unicauca.bancopreguntas.revision.domain.model.PreguntaPendiente;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Revision;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IDirectorioUsuarios;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IPreguntaPendienteRepositorio;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IPublicadorEventos;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IRevisionRepositorio;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.UsuarioInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Dobles de prueba en memoria para probar el dominio de ms-revision sin Spring ni RabbitMQ. */
public final class Dobles {

    private Dobles() {
    }

    public static PreguntaEnviadaARevisionEvento eventoEnvio(String id, String autor) {
        return new PreguntaEnviadaARevisionEvento(id, "A-0231", "¿Cuál es la conclusión?",
                "Probabilidad", "Razonamiento Cuantitativo", autor, LocalDateTime.now());
    }

    public static class PendientesMemoria implements IPreguntaPendienteRepositorio {
        public final Map<String, PreguntaPendiente> datos = new LinkedHashMap<>();

        public PreguntaPendiente guardar(PreguntaPendiente p) {
            datos.put(p.getPreguntaId(), p);
            return p;
        }

        public Optional<PreguntaPendiente> buscarPorId(String id) {
            return Optional.ofNullable(datos.get(id));
        }

        public List<PreguntaPendiente> listarSinAsignar() {
            return datos.values().stream().filter(p -> !p.isAsignada()).toList();
        }
    }

    public static class RevisionesMemoria implements IRevisionRepositorio {
        public final Map<String, Revision> datos = new LinkedHashMap<>();

        public Revision guardar(Revision r) {
            datos.put(r.getId(), r);
            return r;
        }

        public Optional<Revision> buscarPorId(String id) {
            return Optional.ofNullable(datos.get(id));
        }

        public List<Revision> listarPorRevisor(String login) {
            return datos.values().stream().filter(r -> r.getRevisorLogin().equals(login)).toList();
        }

        public List<Revision> listarPorPregunta(String preguntaId) {
            return datos.values().stream().filter(r -> r.getPreguntaId().equals(preguntaId)).toList();
        }

        public List<Revision> listarPorPreguntaYRonda(String preguntaId, int ronda) {
            return datos.values().stream()
                    .filter(r -> r.getPreguntaId().equals(preguntaId) && r.getRonda() == ronda).toList();
        }
    }

    /** Directorio con 4 revisores, 1 autor-revisor, 1 autor y el administrador. */
    public static class DirectorioFijo implements IDirectorioUsuarios {
        private final Map<String, UsuarioInfo> usuarios = new LinkedHashMap<>();

        public DirectorioFijo() {
            agregar("autor1", "AUTOR");
            agregar("autor2", "AUTOR", "REVISOR");
            agregar("revisor1", "REVISOR");
            agregar("revisor2", "REVISOR");
            agregar("revisor3", "REVISOR");
            agregar("revisor4", "REVISOR");
            agregar("admin", "ADMINISTRADOR");
        }

        private void agregar(String login, String... roles) {
            usuarios.put(login, new UsuarioInfo(login, "Nombre " + login, login + "@unicauca.edu.co", Set.of(roles)));
        }

        public Optional<UsuarioInfo> buscar(String login) {
            return Optional.ofNullable(usuarios.get(login));
        }
    }

    public static class PublicadorEspia implements IPublicadorEventos {
        public final List<RevisoresAsignadosEvento> asignaciones = new ArrayList<>();
        public final List<PreguntaEvaluadaEvento> evaluaciones = new ArrayList<>();

        public void revisoresAsignados(RevisoresAsignadosEvento e) {
            asignaciones.add(e);
        }

        public void preguntaEvaluada(PreguntaEvaluadaEvento e) {
            evaluaciones.add(e);
        }
    }
}
