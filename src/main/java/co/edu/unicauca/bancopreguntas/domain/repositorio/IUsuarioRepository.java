package co.edu.unicauca.bancopreguntas.domain.repositorio;

import co.edu.unicauca.bancopreguntas.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository {

    Optional<Usuario> buscarPorLogin(String login);

    List<Usuario> listarPorRol(Rol rol);

    List<Usuario> listarTodos();
}
