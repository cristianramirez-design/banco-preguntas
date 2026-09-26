package co.edu.unicauca.bancopreguntas.acceso.fabrica;

import co.edu.unicauca.bancopreguntas.domain.repositorio.IPreguntaRepository;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IUsuarioRepository;
import co.edu.unicauca.bancopreguntas.domain.servicio.IGeneradorCodigo;

/**
 * Patron GoF Abstract Factory: produce familias completas de objetos de persistencia
 * (repositorios y generador de codigos) que deben ser coherentes entre si.
 * Cambiar el mecanismo de persistencia (memoria, SQLite, ...) se hace en un unico punto.
 */
public abstract class RepositoryFactory {

    public enum Tipo {
        MEMORIA,
        SQLITE
    }

    public abstract IPreguntaRepository crearPreguntaRepository();

    public abstract IUsuarioRepository crearUsuarioRepository();

    public abstract IGeneradorCodigo crearGeneradorCodigo();

    public static RepositoryFactory obtener(Tipo tipo) {
        switch (tipo) {
            case MEMORIA:
                return MemoriaRepositoryFactory.getInstancia();
            case SQLITE:
                return SqliteRepositoryFactory.getInstancia();
            default:
                throw new IllegalArgumentException("Tipo de persistencia no soportado: " + tipo);
        }
    }
}
