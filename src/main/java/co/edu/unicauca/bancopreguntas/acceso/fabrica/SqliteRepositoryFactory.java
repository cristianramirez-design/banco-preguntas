package co.edu.unicauca.bancopreguntas.acceso.fabrica;

import co.edu.unicauca.bancopreguntas.acceso.memoria.UsuarioRepositoryMemoria;
import co.edu.unicauca.bancopreguntas.acceso.sqlite.ConexionSqlite;
import co.edu.unicauca.bancopreguntas.acceso.sqlite.GeneradorCodigoSqlite;
import co.edu.unicauca.bancopreguntas.acceso.sqlite.PreguntaRepositorySqlite;
import co.edu.unicauca.bancopreguntas.acceso.sqlite.UsuarioRepositorySqlite;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IPreguntaRepository;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IUsuarioRepository;
import co.edu.unicauca.bancopreguntas.domain.servicio.IGeneradorCodigo;

/**
 * Familia concreta de la Abstract Factory para SQLite. Los datos quedan en el
 * archivo banco-preguntas.db y sobreviven al cierre de la aplicacion.
 */
public class SqliteRepositoryFactory extends RepositoryFactory {

    public static final String ARCHIVO_POR_DEFECTO = "banco-preguntas.db";

    private static SqliteRepositoryFactory instancia;

    private final IPreguntaRepository preguntaRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IGeneradorCodigo generadorCodigo;

    SqliteRepositoryFactory(String rutaArchivo) {
        ConexionSqlite conexion = new ConexionSqlite(rutaArchivo);
        this.preguntaRepository = new PreguntaRepositorySqlite(conexion);
        this.usuarioRepository = new UsuarioRepositorySqlite(conexion,
                new UsuarioRepositoryMemoria().listarTodos());
        this.generadorCodigo = new GeneradorCodigoSqlite(conexion);
    }

    public static synchronized SqliteRepositoryFactory getInstancia() {
        if (instancia == null) {
            instancia = new SqliteRepositoryFactory(ARCHIVO_POR_DEFECTO);
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
