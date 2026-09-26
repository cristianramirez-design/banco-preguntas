package co.edu.unicauca.bancopreguntas.domain.repositorio;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia. La interfaz vive en el dominio y la implementacion
 * en la capa de acceso a datos: asi el dominio no depende de detalles
 * (principio de Inversion de Dependencias).
 */
public interface IPreguntaRepository {

    Pregunta guardar(Pregunta pregunta);

    boolean actualizar(Pregunta pregunta);

    Optional<Pregunta> buscarPorId(String id);

    List<Pregunta> listarTodas();
}
