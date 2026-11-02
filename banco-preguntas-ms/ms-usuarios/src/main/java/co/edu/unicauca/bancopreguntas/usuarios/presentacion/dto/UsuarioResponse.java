package co.edu.unicauca.bancopreguntas.usuarios.presentacion.dto;

import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Usuario;

import java.util.List;

public record UsuarioResponse(String login, String nombre, String email, List<String> roles) {

    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getLogin(), u.getNombre(), u.getEmail(),
                u.getRoles().stream().map(Rol::name).sorted().toList());
    }
}
