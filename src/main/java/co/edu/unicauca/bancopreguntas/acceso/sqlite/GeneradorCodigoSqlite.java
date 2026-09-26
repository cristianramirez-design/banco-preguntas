package co.edu.unicauca.bancopreguntas.acceso.sqlite;

import co.edu.unicauca.bancopreguntas.domain.servicio.IGeneradorCodigo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Genera el siguiente codigo A-NNNN a partir del mayor codigo guardado,
 * para que los codigos no se repitan entre ejecuciones de la aplicacion.
 */
public class GeneradorCodigoSqlite implements IGeneradorCodigo {

    private static final String PREFIJO = "A";
    private static final int INICIO = 227;

    private final Connection conexion;

    public GeneradorCodigoSqlite(ConexionSqlite conexionSqlite) {
        this.conexion = conexionSqlite.getConexion();
    }

    @Override
    public synchronized String siguiente() {
        String sql = "SELECT MAX(CAST(SUBSTR(codigo, 3) AS INTEGER)) FROM pregunta WHERE codigo LIKE 'A-%'";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            int mayor = rs.next() ? rs.getInt(1) : 0;
            int siguiente = rs.wasNull() || mayor < INICIO ? INICIO : mayor + 1;
            return String.format("%s-%04d", PREFIJO, siguiente);
        } catch (SQLException e) {
            throw new PersistenciaException("Error generando el codigo de la pregunta", e);
        }
    }
}
