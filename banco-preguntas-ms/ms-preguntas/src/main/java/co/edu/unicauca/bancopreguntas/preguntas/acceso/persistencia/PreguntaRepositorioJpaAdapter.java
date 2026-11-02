package co.edu.unicauca.bancopreguntas.preguntas.acceso.persistencia;

import co.edu.unicauca.bancopreguntas.preguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.Opcion;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.repositorio.IPreguntaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Patron GoF Adapter: adapta el repositorio Spring Data JPA a la interfaz IPreguntaRepository
 * que define el dominio. El dominio no sabe que existe JPA.
 */
@Component
public class PreguntaRepositorioJpaAdapter implements IPreguntaRepository {

    private final PreguntaJpaRepository jpa;

    public PreguntaRepositorioJpaAdapter(PreguntaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public Pregunta guardar(Pregunta pregunta) {
        jpa.save(aEntidad(pregunta));
        return pregunta;
    }

    @Override
    @Transactional
    public boolean actualizar(Pregunta pregunta) {
        if (!jpa.existsById(pregunta.getId())) {
            return false;
        }
        jpa.save(aEntidad(pregunta));
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pregunta> buscarPorId(String id) {
        return jpa.findById(id).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pregunta> listarTodas() {
        List<Pregunta> resultado = new ArrayList<>();
        for (PreguntaJpaEntity entidad : jpa.findAll()) {
            resultado.add(aDominio(entidad));
        }
        return resultado;
    }

    // ------------------------------------------------------------------

    private PreguntaJpaEntity aEntidad(Pregunta p) {
        PreguntaJpaEntity e = new PreguntaJpaEntity();
        e.setId(p.getId());
        e.setCodigo(p.getCodigo());
        e.setContexto(p.getContexto());
        e.setEnunciado(p.getEnunciado());
        List<OpcionEmbeddable> opciones = new ArrayList<>();
        for (Opcion opcion : p.getOpciones()) {
            opciones.add(new OpcionEmbeddable(opcion.getTexto(), opcion.esCorrecta()));
        }
        e.setOpciones(opciones);
        e.setJustificacion(p.getJustificacion());
        e.setBibliografia(p.getBibliografia());
        e.setCompetencia(p.getCompetencia());
        e.setTema(p.getTema());
        e.setSubtema(p.getSubtema());
        e.setNivelDificultad(p.getNivelDificultad() == null ? null : p.getNivelDificultad().name());
        e.setAutorLogin(p.getAutorLogin());
        e.setFechaCreacion(p.getFechaCreacion());
        e.setEstado(p.getEstado().getNombre());
        e.setFechaEnvioRevision(p.getFechaEnvioRevision());
        e.setRevisores(new ArrayList<>(p.getRevisores()));
        return e;
    }

    private Pregunta aDominio(PreguntaJpaEntity e) {
        List<Opcion> opciones = new ArrayList<>();
        for (OpcionEmbeddable opcion : e.getOpciones()) {
            opciones.add(Opcion.nueva(opcion.getTexto(), opcion.isCorrecta()));
        }
        return new Pregunta.Builder()
                .id(e.getId())
                .codigo(e.getCodigo())
                .contexto(e.getContexto())
                .enunciado(e.getEnunciado())
                .opciones(opciones)
                .justificacion(e.getJustificacion())
                .bibliografia(e.getBibliografia())
                .competencia(e.getCompetencia())
                .tema(e.getTema())
                .subtema(e.getSubtema())
                .nivelDificultad(e.getNivelDificultad() == null ? null : NivelDificultad.valueOf(e.getNivelDificultad()))
                .autorLogin(e.getAutorLogin())
                .fechaCreacion(e.getFechaCreacion())
                .estado(EstadosPregunta.porNombre(e.getEstado()))
                .fechaEnvioRevision(e.getFechaEnvioRevision())
                .revisores(new ArrayList<>(e.getRevisores()))
                .construir();
    }
}
