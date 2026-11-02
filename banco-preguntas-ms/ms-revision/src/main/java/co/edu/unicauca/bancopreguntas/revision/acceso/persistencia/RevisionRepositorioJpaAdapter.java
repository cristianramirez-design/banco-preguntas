package co.edu.unicauca.bancopreguntas.revision.acceso.persistencia;

import co.edu.unicauca.bancopreguntas.revision.domain.model.EstadoRevision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Revision;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IRevisionRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Patron Adapter: adapta Spring Data JPA al puerto del dominio. */
@Repository
public class RevisionRepositorioJpaAdapter implements IRevisionRepositorio {

    private final RevisionJpaRepository jpa;

    public RevisionRepositorioJpaAdapter(RevisionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Revision guardar(Revision r) {
        jpa.save(new RevisionJpaEntity(r.getId(), r.getPreguntaId(), r.getCodigoPregunta(), r.getEnunciado(),
                r.getRonda(), r.getRevisorLogin(), r.getFechaAsignacion(), r.getEstado().name(),
                r.getObservaciones(), r.getFechaEvaluacion()));
        return r;
    }

    @Override
    public Optional<Revision> buscarPorId(String id) {
        return jpa.findById(id).map(this::aDominio);
    }

    @Override
    public List<Revision> listarPorRevisor(String revisorLogin) {
        return jpa.findByRevisorLogin(revisorLogin).stream().map(this::aDominio).toList();
    }

    @Override
    public List<Revision> listarPorPregunta(String preguntaId) {
        return jpa.findByPreguntaId(preguntaId).stream().map(this::aDominio).toList();
    }

    @Override
    public List<Revision> listarPorPreguntaYRonda(String preguntaId, int ronda) {
        return jpa.findByPreguntaIdAndRonda(preguntaId, ronda).stream().map(this::aDominio).toList();
    }

    private Revision aDominio(RevisionJpaEntity e) {
        return new Revision(e.getId(), e.getPreguntaId(), e.getCodigoPregunta(), e.getEnunciado(), e.getRonda(),
                e.getRevisorLogin(), e.getFechaAsignacion(), EstadoRevision.valueOf(e.getEstado()),
                e.getObservaciones(), e.getFechaEvaluacion());
    }
}
