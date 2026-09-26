package co.edu.unicauca.bancopreguntas.acceso.sqlite;

import co.edu.unicauca.bancopreguntas.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.domain.model.Usuario;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IUsuarioRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementacion JDBC/SQLite del puerto IUsuarioRepository. */
public class UsuarioRepositorySqlite implements IUsuarioRepository {

    private final Connection conexion;

    /**
     * @param usuariosIniciales usuarios que se registran si la tabla esta vacia
     *                          (primera ejecucion de la aplicacion).
     */
    public UsuarioRepositorySqlite(ConexionSqlite conexionSqlite, List<Usuario> usuariosIniciales) {
        this.conexion = conexionSqlite.getConexion();
        if (contar() == 0) {
            usuariosIniciales.forEach(this::registrar);
        }
    }

    public synchronized void registrar(Usuario usuario) {
        try {
            try (PreparedStatement ps = conexion.prepareStatement(
                    "INSERT OR REPLACE INTO usuario (login, nombre, email) VALUES (?,?,?)")) {
                ps.setString(1, usuario.getLogin().toLowerCase());
                ps.setString(2, usuario.getNombre());
                ps.setString(3, usuario.getEmail());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conexion.prepareStatement(
                    "INSERT OR IGNORE INTO usuario_rol (login, rol) VALUES (?,?)")) {
                for (Rol rol : usuario.getRoles()) {
                    ps.setString(1, usuario.getLogin().toLowerCase());
                    ps.setString(2, rol.name());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error registrando el usuario " + usuario.getLogin(), e);
        }
    }

    @Override
    public synchronized Optional<Usuario> buscarPorLogin(String login) {
        if (login == null) {
            return Optional.empty();
        }
        List<Usuario> encontrados = consultar("SELECT login, nombre, email FROM usuario WHERE login = ?",
                login.toLowerCase());
        return encontrados.stream().findFirst();
    }

    @Override
    public synchronized List<Usuario> listarPorRol(Rol rol) {
        return consultar("SELECT u.login, u.nombre, u.email FROM usuario u"
                + " JOIN usuario_rol r ON r.login = u.login WHERE r.rol = ? ORDER BY u.rowid", rol.name());
    }

    @Override
    public synchronized List<Usuario> listarTodos() {
        return consultar("SELECT login, nombre, email FROM usuario ORDER BY rowid");
    }

    private List<Usuario> consultar(String sql, String... parametros) {
        List<Usuario> resultado = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setString(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String login = rs.getString("login");
                    resultado.add(new Usuario(login, rs.getString("nombre"), rs.getString("email"),
                            roles(login)));
                }
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error consultando usuarios", e);
        }
    }

    private Rol[] roles(String login) throws SQLException {
        List<Rol> roles = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement("SELECT rol FROM usuario_rol WHERE login = ?")) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(Rol.valueOf(rs.getString("rol")));
                }
            }
        }
        return roles.toArray(new Rol[0]);
    }

    private int contar() {
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM usuario")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error contando usuarios", e);
        }
    }
}
