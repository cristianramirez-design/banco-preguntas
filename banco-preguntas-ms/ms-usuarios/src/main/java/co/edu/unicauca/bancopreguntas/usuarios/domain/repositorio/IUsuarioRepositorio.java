package co.edu.unicauca.bancopreguntas.usuarios.domain.repositorio;

import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de usuarios (lo implementa la capa de acceso a datos). */
public interface IUsuarioRepositorio {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorLogin(String login);

    List<Usuario> listarTodos();

    long contar();
}
