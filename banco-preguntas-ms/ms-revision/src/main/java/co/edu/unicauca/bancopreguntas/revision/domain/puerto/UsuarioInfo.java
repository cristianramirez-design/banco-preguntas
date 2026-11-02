package co.edu.unicauca.bancopreguntas.revision.domain.puerto;

import java.util.Set;

/** Lo que ms-revision necesita saber de un usuario (lo obtiene de ms-usuarios). */
public record UsuarioInfo(String login, String nombre, String email, Set<String> roles) {

    public boolean esRevisor() {
        return roles != null && roles.contains("REVISOR");
    }

    public boolean esAdministrador() {
        return roles != null && roles.contains("ADMINISTRADOR");
    }
}
