package co.edu.unicauca.bancopreguntas.revision.acceso.persistencia;

import co.edu.unicauca.bancopreguntas.revision.domain.model.PreguntaPendiente;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IPreguntaPendienteRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Patron Adapter: adapta Spring Data JPA al puerto del dominio. */
@Repository
public class PreguntaPendienteRepositorioJpaAdapter implements IPreguntaPendienteRepositorio {

    private final PreguntaPendienteJpaRepository jpa;

    public PreguntaPendienteRepositorioJpaAdapter(PreguntaPendienteJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PreguntaPendiente guardar(PreguntaPendiente p) {
        jpa.save(new PreguntaPendienteJpaEntity(p.getPreguntaId(), p.getCodigo(), p.getEnunciado(), p.getTema(),
                p.getCompetencia(), p.getAutorLogin(), p.getFechaEnvio(), p.getRonda(), p.isAsignada(),
                p.getAdministradorLogin(), p.getFechaAsignacion()));
        return p;
    }

    @Override
    public Optional<PreguntaPendiente> buscarPorId(String preguntaId) {
        return jpa.findById(preguntaId).map(this::aDominio);
    }

    @Override
    public List<PreguntaPendiente> listarSinAsignar() {
        return jpa.findByAsignadaFalse().stream().map(this::aDominio).toList();
    }

    private PreguntaPendiente aDominio(PreguntaPendienteJpaEntity e) {
        return new PreguntaPendiente(e.getPreguntaId(), e.getCodigo(), e.getEnunciado(), e.getTema(),
                e.getCompetencia(), e.getAutorLogin(), e.getFechaEnvio(), e.getRonda(), e.isAsignada(),
                e.getAdministradorLogin(), e.getFechaAsignacion());
    }
}
