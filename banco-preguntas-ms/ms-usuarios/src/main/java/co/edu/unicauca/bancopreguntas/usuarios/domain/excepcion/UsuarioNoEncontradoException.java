package co.edu.unicauca.bancopreguntas.usuarios.domain.excepcion;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(String login) {
        super("No existe el usuario: " + login);
    }
}
