package co.edu.unicauca.bancopreguntas.acceso.memoria;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IPreguntaRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementacion en memoria del repositorio de preguntas.
 * Cambiar a JDBC o JPA solo exige otra clase que implemente IPreguntaRepository:
 * ni el dominio ni la presentacion se enteran (principio Open/Closed y DIP).
 */
public class PreguntaRepositoryMemoria implements IPreguntaRepository {

    private final Map<String, Pregunta> almacen = new LinkedHashMap<>();

    @Override
    public Pregunta guardar(Pregunta pregunta) {
        almacen.put(pregunta.getId(), pregunta);
        return pregunta;
    }

    @Override
    public boolean actualizar(Pregunta pregunta) {
        if (!almacen.containsKey(pregunta.getId())) {
            return false;
        }
        almacen.put(pregunta.getId(), pregunta);
        return true;
    }

    @Override
    public Optional<Pregunta> buscarPorId(String id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<Pregunta> listarTodas() {
        return new ArrayList<>(almacen.values());
    }
}
