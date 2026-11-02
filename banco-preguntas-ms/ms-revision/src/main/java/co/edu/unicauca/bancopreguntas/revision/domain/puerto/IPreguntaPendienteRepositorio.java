package co.edu.unicauca.bancopreguntas.revision.domain.puerto;

import co.edu.unicauca.bancopreguntas.revision.domain.model.PreguntaPendiente;

import java.util.List;
import java.util.Optional;

public interface IPreguntaPendienteRepositorio {

    PreguntaPendiente guardar(PreguntaPendiente pendiente);

    Optional<PreguntaPendiente> buscarPorId(String preguntaId);

    List<PreguntaPendiente> listarSinAsignar();
}
