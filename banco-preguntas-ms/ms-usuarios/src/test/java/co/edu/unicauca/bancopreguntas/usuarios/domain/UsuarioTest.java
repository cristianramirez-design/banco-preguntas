package co.edu.unicauca.bancopreguntas.usuarios.domain;

import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {

    @Test
    @DisplayName("Un usuario puede tener varios roles")
    void variosRoles() {
        Usuario u = Usuario.de("autor2", "Carlos", "c@u.co", Rol.AUTOR, Rol.REVISOR);
        assertTrue(u.tieneRol(Rol.AUTOR));
        assertTrue(u.tieneRol(Rol.REVISOR));
        assertFalse(u.tieneRol(Rol.ADMINISTRADOR));
    }

    @Test
    @DisplayName("El login se normaliza a minusculas y sin espacios")
    void normalizaLogin() {
        assertEquals("autor1", Usuario.de("  AUTOR1 ", "Ana", "a@u.co", Rol.AUTOR).getLogin());
    }

    @Test
    @DisplayName("No se permite un usuario sin login o sin roles")
    void datosObligatorios() {
        assertThrows(IllegalArgumentException.class, () -> Usuario.de(" ", "Ana", "a@u.co", Rol.AUTOR));
        assertThrows(IllegalArgumentException.class, () -> new Usuario("ana", "Ana", "a@u.co", Set.of()));
    }

    @Test
    @DisplayName("Los roles expuestos no se pueden modificar desde afuera")
    void rolesInmutables() {
        Usuario u = Usuario.de("ana", "Ana", "a@u.co", Rol.AUTOR);
        assertThrows(UnsupportedOperationException.class, () -> u.getRoles().add(Rol.ADMINISTRADOR));
    }

    @Test
    @DisplayName("Dos usuarios son iguales si tienen el mismo login")
    void igualdad() {
        assertEquals(Usuario.de("ana", "Ana", "a@u.co", Rol.AUTOR),
                Usuario.de("ANA", "Otra", "x@u.co", Rol.REVISOR));
    }
}
