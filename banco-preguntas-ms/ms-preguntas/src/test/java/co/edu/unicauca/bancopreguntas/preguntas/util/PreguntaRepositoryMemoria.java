package co.edu.unicauca.bancopreguntas.preguntas.util;

import co.edu.unicauca.bancopreguntas.preguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.repositorio.IPreguntaRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Doble de prueba: repositorio en memoria para probar el dominio sin base de datos. */
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
