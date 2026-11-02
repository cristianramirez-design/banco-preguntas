package co.edu.unicauca.bancopreguntas.revision.domain.puerto;

import co.edu.unicauca.bancopreguntas.revision.domain.model.Revision;

import java.util.List;
import java.util.Optional;

public interface IRevisionRepositorio {

    Revision guardar(Revision revision);

    Optional<Revision> buscarPorId(String id);

    List<Revision> listarPorRevisor(String revisorLogin);

    List<Revision> listarPorPregunta(String preguntaId);

    List<Revision> listarPorPreguntaYRonda(String preguntaId, int ronda);
}
