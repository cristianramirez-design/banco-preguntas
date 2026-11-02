package co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto;

import java.util.Optional;

public interface IDirectorioUsuarios {

    Optional<UsuarioInfo> buscar(String login);
}
