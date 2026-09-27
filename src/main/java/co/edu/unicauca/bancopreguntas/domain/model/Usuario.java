package co.edu.unicauca.bancopreguntas.domain.model;

import java.util.EnumSet;
import java.util.Set;

public class Usuario {

    private final String login;
    private final String nombre;
    private final String email;
    private final Set<Rol> roles;

    public Usuario(String login, String nombre, String email, Rol... roles) {
        this.login = login;
        this.nombre = nombre;
        this.email = email;
        this.roles = roles.length == 0 ? EnumSet.noneOf(Rol.class) : EnumSet.copyOf(Set.of(roles));
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

    public boolean tieneRol(Rol rol) {
        return roles.contains(rol);
    }

    @Override
    public String toString() {
        return nombre + " (" + login + ")";
    }
}
