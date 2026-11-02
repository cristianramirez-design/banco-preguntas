package co.edu.unicauca.bancopreguntas.usuarios.domain.model;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/** Docente que usa el banco de preguntas. Un usuario puede tener varios roles. */
public class Usuario {

    private final String login;
    private final String nombre;
    private final String email;
    private final Set<Rol> roles;

    public Usuario(String login, String nombre, String email, Set<Rol> roles) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("El login es obligatorio.");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("El usuario debe tener al menos un rol.");
        }
        this.login = login.trim().toLowerCase();
        this.nombre = nombre;
        this.email = email;
        this.roles = EnumSet.copyOf(roles);
    }

    public static Usuario de(String login, String nombre, String email, Rol... roles) {
        return new Usuario(login, nombre, email, Set.of(roles));
    }

    public boolean tieneRol(Rol rol) {
        return roles.contains(rol);
    }

    public String getLogin() {
        return login;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public Set<Rol> getRoles() {
        return Set.copyOf(roles);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Usuario otro && login.equals(otro.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}
