package co.edu.unicauca.bancopreguntas.domain;

import co.edu.unicauca.bancopreguntas.acceso.memoria.UsuarioRepositoryMemoria;
import co.edu.unicauca.bancopreguntas.domain.model.Opcion;
import co.edu.unicauca.bancopreguntas.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.domain.model.Usuario;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IUsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {

    @Test
    @DisplayName("Un usuario puede tener varios roles")
    void rolesMultiples() {
        Usuario usuario = new Usuario("autor2", "Carlos", "c@unicauca.edu.co", Rol.AUTOR, Rol.REVISOR);
        assertTrue(usuario.tieneRol(Rol.AUTOR));
        assertTrue(usuario.tieneRol(Rol.REVISOR));
        assertFalse(usuario.tieneRol(Rol.ADMINISTRADOR));
    }

    @Test
    @DisplayName("La busqueda por login no distingue mayusculas")
    void busquedaPorLogin() {
        IUsuarioRepository repositorio = new UsuarioRepositoryMemoria();
        assertTrue(repositorio.buscarPorLogin("ADMIN").isPresent());
        assertTrue(repositorio.buscarPorLogin("noexiste").isEmpty());
    }

    @Test
    @DisplayName("El repositorio filtra correctamente por rol")
    void listadoPorRol() {
        IUsuarioRepository repositorio = new UsuarioRepositoryMemoria();
        assertEquals(1, repositorio.listarPorRol(Rol.ADMINISTRADOR).size());
        assertTrue(repositorio.listarPorRol(Rol.REVISOR).size() >= 3);
    }

    @Test
    @DisplayName("Dos opciones con el mismo texto se consideran iguales")
    void igualdadDeOpciones() {
        assertEquals(Opcion.correcta("Bogota"), Opcion.distractor("bogota"));
        assertFalse(Opcion.correcta("Bogota").equals(Opcion.distractor("Cali")));
    }
}
