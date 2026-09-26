package co.edu.unicauca.bancopreguntas.acceso.sqlite;

import co.edu.unicauca.bancopreguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.domain.model.Opcion;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IPreguntaRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JDBC/SQLite del puerto IPreguntaRepository.
 * El dominio y la presentacion no cambiaron para soportarla (OCP + DIP):
 * es la respuesta del escenario de calidad de modificabilidad.
 */
public class PreguntaRepositorySqlite implements IPreguntaRepository {

    private static final String COLUMNAS = "id, codigo, contexto, enunciado, justificacion, bibliografia,"
            + " competencia, tema, subtema, nivel, autor_login, fecha_creacion, estado, fecha_envio_revision";

    private final Connection conexion;

    public PreguntaRepositorySqlite(ConexionSqlite conexionSqlite) {
        this.conexion = conexionSqlite.getConexion();
    }

    @Override
    public synchronized Pregunta guardar(Pregunta pregunta) {
        escribir(pregunta);
        return pregunta;
    }

    @Override
    public synchronized boolean actualizar(Pregunta pregunta) {
        if (buscarPorId(pregunta.getId()).isEmpty()) {
            return false;
        }
        escribir(pregunta);
        return true;
    }

    @Override
    public synchronized Optional<Pregunta> buscarPorId(String id) {
        String sql = "SELECT " + COLUMNAS + " FROM pregunta WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error consultando la pregunta " + id, e);
        }
    }

    @Override
    public synchronized List<Pregunta> listarTodas() {
        String sql = "SELECT " + COLUMNAS + " FROM pregunta ORDER BY fecha_creacion, codigo";
        List<Pregunta> resultado = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error listando las preguntas", e);
        }
    }

    // ------------------------------------------------------------------

    /** Inserta o reemplaza la pregunta con sus opciones y revisores en una transaccion. */
    private void escribir(Pregunta p) {
        try {
            conexion.setAutoCommit(false);
            try (PreparedStatement ps = conexion.prepareStatement(
                    "INSERT OR REPLACE INTO pregunta (" + COLUMNAS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                ps.setString(1, p.getId());
                ps.setString(2, p.getCodigo());
                ps.setString(3, p.getContexto());
                ps.setString(4, p.getEnunciado());
                ps.setString(5, p.getJustificacion());
                ps.setString(6, p.getBibliografia());
                ps.setString(7, p.getCompetencia());
                ps.setString(8, p.getTema());
                ps.setString(9, p.getSubtema());
                ps.setString(10, p.getNivelDificultad() == null ? null : p.getNivelDificultad().name());
                ps.setString(11, p.getAutorLogin());
                ps.setString(12, texto(p.getFechaCreacion()));
                ps.setString(13, p.getEstado().getNombre());
                ps.setString(14, texto(p.getFechaEnvioRevision()));
                ps.executeUpdate();
            }
            borrarHijos("opcion", p.getId());
            borrarHijos("revisor_asignado", p.getId());
            try (PreparedStatement ps = conexion.prepareStatement(
                    "INSERT INTO opcion (pregunta_id, orden, texto, correcta) VALUES (?,?,?,?)")) {
                List<Opcion> opciones = p.getOpciones();
                for (int i = 0; i < opciones.size(); i++) {
                    ps.setString(1, p.getId());
                    ps.setInt(2, i);
                    ps.setString(3, opciones.get(i).getTexto());
                    ps.setInt(4, opciones.get(i).esCorrecta() ? 1 : 0);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            try (PreparedStatement ps = conexion.prepareStatement(
                    "INSERT INTO revisor_asignado (pregunta_id, login) VALUES (?,?)")) {
                for (String login : p.getRevisores()) {
                    ps.setString(1, p.getId());
                    ps.setString(2, login);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conexion.commit();
        } catch (SQLException e) {
            deshacer();
            throw new PersistenciaException("Error guardando la pregunta " + p.getCodigo(), e);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException ignorada) {
                // La conexion sigue siendo utilizable.
            }
        }
    }

    private void borrarHijos(String tabla, String preguntaId) throws SQLException {
        try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM " + tabla + " WHERE pregunta_id = ?")) {
            ps.setString(1, preguntaId);
            ps.executeUpdate();
        }
    }

    private Pregunta mapear(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String nivel = rs.getString("nivel");
        return new Pregunta.Builder()
                .id(id)
                .codigo(rs.getString("codigo"))
                .contexto(rs.getString("contexto"))
                .enunciado(rs.getString("enunciado"))
                .opciones(cargarOpciones(id))
                .justificacion(rs.getString("justificacion"))
                .bibliografia(rs.getString("bibliografia"))
                .competencia(rs.getString("competencia"))
                .tema(rs.getString("tema"))
                .subtema(rs.getString("subtema"))
                .nivelDificultad(nivel == null ? null : NivelDificultad.valueOf(nivel))
                .autorLogin(rs.getString("autor_login"))
                .fechaCreacion(fecha(rs.getString("fecha_creacion")))
                .estado(EstadosPregunta.porNombre(rs.getString("estado")))
                .fechaEnvioRevision(fecha(rs.getString("fecha_envio_revision")))
                .revisores(cargarRevisores(id))
                .construir();
    }

    private List<Opcion> cargarOpciones(String preguntaId) throws SQLException {
        List<Opcion> opciones = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT texto, correcta FROM opcion WHERE pregunta_id = ? ORDER BY orden")) {
            ps.setString(1, preguntaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    opciones.add(Opcion.nueva(rs.getString("texto"), rs.getInt("correcta") == 1));
                }
            }
        }
        return opciones;
    }

    private List<String> cargarRevisores(String preguntaId) throws SQLException {
        List<String> revisores = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT login FROM revisor_asignado WHERE pregunta_id = ? ORDER BY login")) {
            ps.setString(1, preguntaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    revisores.add(rs.getString("login"));
                }
            }
        }
        return revisores;
    }

    private void deshacer() {
        try {
            conexion.rollback();
        } catch (SQLException ignorada) {
            // Se reporta la excepcion original.
        }
    }

    private static String texto(LocalDateTime fecha) {
        return fecha == null ? null : fecha.toString();
    }

    private static LocalDateTime fecha(String valor) {
        return valor == null ? null : LocalDateTime.parse(valor);
    }
}
