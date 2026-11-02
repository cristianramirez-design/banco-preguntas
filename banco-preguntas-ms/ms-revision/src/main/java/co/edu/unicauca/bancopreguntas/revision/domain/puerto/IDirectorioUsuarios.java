package co.edu.unicauca.bancopreguntas.revision.domain.puerto;

import java.util.Optional;

/**
 * Puerto hacia ms-usuarios. La implementacion hace una llamada REST sincrona; el dominio
 * solo ve esta interfaz.
 */
public interface IDirectorioUsuarios {

    Optional<UsuarioInfo> buscar(String login);
}
