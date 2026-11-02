package co.edu.unicauca.bancopreguntas.usuarios.acceso.persistencia;

import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Usuario;
import co.edu.unicauca.bancopreguntas.usuarios.domain.repositorio.IUsuarioRepositorio;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Patron GoF Adapter: Spring Data JPA detras del puerto IUsuarioRepositorio. */
@Component
public class UsuarioRepositorioJpaAdapter implements IUsuarioRepositorio {

    private final UsuarioJpaRepository jpa;

    public UsuarioRepositorioJpaAdapter(UsuarioJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        UsuarioJpaEntity e = new UsuarioJpaEntity();
        e.setLogin(usuario.getLogin());
        e.setNombre(usuario.getNombre());
        e.setEmail(usuario.getEmail());
        Set<String> roles = new HashSet<>();
        usuario.getRoles().forEach(r -> roles.add(r.name()));
        e.setRoles(roles);
        jpa.save(e);
        return usuario;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorLogin(String login) {
        return jpa.findById(login).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return jpa.findAll().stream().map(this::aDominio).toList();
    }

    @Override
    public long contar() {
        return jpa.count();
    }

    private Usuario aDominio(UsuarioJpaEntity e) {
        Set<Rol> roles = EnumSet.noneOf(Rol.class);
        e.getRoles().forEach(r -> roles.add(Rol.valueOf(r)));
        return new Usuario(e.getLogin(), e.getNombre(), e.getEmail(), roles);
    }
}
