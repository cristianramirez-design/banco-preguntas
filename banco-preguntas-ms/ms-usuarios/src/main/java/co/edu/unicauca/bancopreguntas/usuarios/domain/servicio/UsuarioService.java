package co.edu.unicauca.bancopreguntas.usuarios.domain.servicio;

import co.edu.unicauca.bancopreguntas.usuarios.domain.excepcion.UsuarioNoEncontradoException;
import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Usuario;
import co.edu.unicauca.bancopreguntas.usuarios.domain.repositorio.IUsuarioRepositorio;

import java.util.Comparator;
import java.util.List;

/** Casos de uso de la Gestion de Usuarios. */
public class UsuarioService {

    private final IUsuarioRepositorio repositorio;

    public UsuarioService(IUsuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /** Lista los usuarios; si se indica un rol, solo los que lo tienen (p. ej. REVISOR para la HU-4). */
    public List<Usuario> listar(Rol rol) {
        return repositorio.listarTodos().stream()
                .filter(u -> rol == null || u.tieneRol(rol))
                .sorted(Comparator.comparing(Usuario::getLogin))
                .toList();
    }

    public Usuario obtener(String login) {
        if (login == null) {
            throw new UsuarioNoEncontradoException("(vacio)");
        }
        return repositorio.buscarPorLogin(login.trim().toLowerCase())
                .orElseThrow(() -> new UsuarioNoEncontradoException(login));
    }

    /**
     * Inicio de sesion simplificado para el proyecto de curso: basta con un login existente.
     * La autenticacion con contrasena y JWT queda para el siguiente corte.
     */
    public Usuario iniciarSesion(String login) {
        return obtener(login);
    }

    /** Registra los usuarios de prueba si la base esta vacia. */
    public void cargarIniciales(List<Usuario> usuarios) {
        if (repositorio.contar() == 0) {
            usuarios.forEach(repositorio::guardar);
        }
    }
}
