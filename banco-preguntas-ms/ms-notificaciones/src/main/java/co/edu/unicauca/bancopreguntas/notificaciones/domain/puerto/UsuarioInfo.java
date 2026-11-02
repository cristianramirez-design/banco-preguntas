package co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto;

import java.util.Set;

public record UsuarioInfo(String login, String nombre, String email, Set<String> roles) {
}
