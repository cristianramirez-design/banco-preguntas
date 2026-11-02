package co.edu.unicauca.bancopreguntas.usuarios.domain;

import co.edu.unicauca.bancopreguntas.usuarios.domain.excepcion.UsuarioNoEncontradoException;
import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Usuario;
import co.edu.unicauca.bancopreguntas.usuarios.domain.repositorio.IUsuarioRepositorio;
import co.edu.unicauca.bancopreguntas.usuarios.domain.servicio.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsuarioServiceTest {

    /** Doble de prueba en memoria. */
    static class RepositorioMemoria implements IUsuarioRepositorio {
        final Map<String, Usuario> datos = new LinkedHashMap<>();

        public Usuario guardar(Usuario u) {
            datos.put(u.getLogin(), u);
            return u;
        }

        public Optional<Usuario> buscarPorLogin(String login) {
            return Optional.ofNullable(datos.get(login));
        }

        public List<Usuario> listarTodos() {
            return new ArrayList<>(datos.values());
        }

        public long contar() {
            return datos.size();
        }
    }

    private RepositorioMemoria repositorio;
    private UsuarioService servicio;

    @BeforeEach
    void inicializar() {
        repositorio = new RepositorioMemoria();
        servicio = new UsuarioService(repositorio);
        servicio.cargarIniciales(List.of(
                Usuario.de("autor1", "Ana", "ana@u.co", Rol.AUTOR),
                Usuario.de("autor2", "Carlos", "carlos@u.co", Rol.AUTOR, Rol.REVISOR),
                Usuario.de("revisor1", "Luisa", "luisa@u.co", Rol.REVISOR),
                Usuario.de("admin", "Coordinacion", "admin@u.co", Rol.ADMINISTRADOR)));
    }

    @Test
    @DisplayName("Lista los revisores para la pantalla de asignacion (HU-4)")
    void listarRevisores() {
        List<Usuario> revisores = servicio.listar(Rol.REVISOR);
        assertEquals(List.of("autor2", "revisor1"), revisores.stream().map(Usuario::getLogin).toList());
    }

    @Test
    @DisplayName("Sin rol lista todos los usuarios ordenados por login")
    void listarTodos() {
        assertEquals(4, servicio.listar(null).size());
        assertEquals("admin", servicio.listar(null).get(0).getLogin());
    }

    @Test
    @DisplayName("Inicia sesion con un login existente sin importar mayusculas")
    void iniciarSesion() {
        assertEquals("Ana", servicio.iniciarSesion(" Autor1 ").getNombre());
    }

    @Test
    @DisplayName("Un login inexistente lanza UsuarioNoEncontradoException")
    void loginInexistente() {
        assertThrows(UsuarioNoEncontradoException.class, () -> servicio.iniciarSesion("nadie"));
        assertThrows(UsuarioNoEncontradoException.class, () -> servicio.obtener(null));
    }

    @Test
    @DisplayName("Los usuarios iniciales solo se cargan si la base esta vacia")
    void cargaInicialUnaVez() {
        servicio.cargarIniciales(List.of(Usuario.de("otro", "Otro", "o@u.co", Rol.AUTOR)));
        assertEquals(4, repositorio.contar());
    }
}
