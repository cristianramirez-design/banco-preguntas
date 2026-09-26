package co.edu.unicauca.bancopreguntas.acceso.fabrica;

import co.edu.unicauca.bancopreguntas.acceso.memoria.GeneradorCodigoSecuencial;
import co.edu.unicauca.bancopreguntas.acceso.memoria.PreguntaRepositoryMemoria;
import co.edu.unicauca.bancopreguntas.acceso.memoria.UsuarioRepositoryMemoria;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IPreguntaRepository;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IUsuarioRepository;
import co.edu.unicauca.bancopreguntas.domain.servicio.IGeneradorCodigo;

/**
 * Patron GoF Singleton: una sola fabrica garantiza que todas las vistas
 * compartan la misma instancia de cada repositorio en memoria.
 */
public class MemoriaRepositoryFactory extends RepositoryFactory {

    private static MemoriaRepositoryFactory instancia;

    private final IPreguntaRepository preguntaRepository = new PreguntaRepositoryMemoria();
    private final IUsuarioRepository usuarioRepository = new UsuarioRepositoryMemoria();
    private final IGeneradorCodigo generadorCodigo = new GeneradorCodigoSecuencial();

    private MemoriaRepositoryFactory() {
    }

    public static synchronized MemoriaRepositoryFactory getInstancia() {
        if (instancia == null) {
            instancia = new MemoriaRepositoryFactory();
        }
        return instancia;
    }

    @Override
    public IPreguntaRepository crearPreguntaRepository() {
        return preguntaRepository;
    }

    @Override
    public IUsuarioRepository crearUsuarioRepository() {
        return usuarioRepository;
    }

    @Override
    public IGeneradorCodigo crearGeneradorCodigo() {
        return generadorCodigo;
    }
}
