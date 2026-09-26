package co.edu.unicauca.bancopreguntas.acceso.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Administra la unica conexion JDBC a la base de datos SQLite y crea el esquema
 * la primera vez. Solo la conocen las clases de la capa de acceso a datos.
 */
public class ConexionSqlite implements AutoCloseable {

    private final Connection conexion;

    public ConexionSqlite(String rutaArchivo) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.conexion = DriverManager.getConnection("jdbc:sqlite:" + rutaArchivo);
            crearEsquema();
        } catch (ClassNotFoundException e) {
            throw new PersistenciaException("Falta el driver sqlite-jdbc en el classpath", e);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo abrir la base de datos " + rutaArchivo, e);
        }
    }

    public Connection getConexion() {
        return conexion;
    }

    private void crearEsquema() throws SQLException {
        try (Statement st = conexion.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("CREATE TABLE IF NOT EXISTS usuario ("
                    + " login TEXT PRIMARY KEY, nombre TEXT NOT NULL, email TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS usuario_rol ("
                    + " login TEXT NOT NULL REFERENCES usuario(login) ON DELETE CASCADE,"
                    + " rol TEXT NOT NULL, PRIMARY KEY (login, rol))");
            st.execute("CREATE TABLE IF NOT EXISTS pregunta ("
                    + " id TEXT PRIMARY KEY, codigo TEXT, contexto TEXT, enunciado TEXT,"
                    + " justificacion TEXT, bibliografia TEXT, competencia TEXT, tema TEXT,"
                    + " subtema TEXT, nivel TEXT, autor_login TEXT, fecha_creacion TEXT,"
                    + " estado TEXT NOT NULL, fecha_envio_revision TEXT)");
            st.execute("CREATE TABLE IF NOT EXISTS opcion ("
                    + " pregunta_id TEXT NOT NULL REFERENCES pregunta(id) ON DELETE CASCADE,"
                    + " orden INTEGER NOT NULL, texto TEXT, correcta INTEGER NOT NULL,"
                    + " PRIMARY KEY (pregunta_id, orden))");
            st.execute("CREATE TABLE IF NOT EXISTS revisor_asignado ("
                    + " pregunta_id TEXT NOT NULL REFERENCES pregunta(id) ON DELETE CASCADE,"
                    + " login TEXT NOT NULL, PRIMARY KEY (pregunta_id, login))");
        }
    }

    @Override
    public void close() {
        try {
            conexion.close();
        } catch (SQLException ignorada) {
            // Se cierra la aplicacion de todas formas.
        }
    }
}
