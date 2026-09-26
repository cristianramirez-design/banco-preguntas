package co.edu.unicauca.bancopreguntas.acceso.memoria;

import co.edu.unicauca.bancopreguntas.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.domain.model.Usuario;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IUsuarioRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UsuarioRepositoryMemoria implements IUsuarioRepository {

    private final Map<String, Usuario> almacen = new LinkedHashMap<>();

    public UsuarioRepositoryMemoria() {
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        registrar(new Usuario("autor1", "Ana María Rojas", "ana.rojas@unicauca.edu.co", Rol.AUTOR));
        registrar(new Usuario("autor2", "Carlos Andrés Díaz", "carlos.diaz@unicauca.edu.co", Rol.AUTOR, Rol.REVISOR));
        registrar(new Usuario("revisor1", "Luisa Fernanda Muñoz", "luisa.munoz@unicauca.edu.co", Rol.REVISOR));
        registrar(new Usuario("revisor2", "Jorge Enrique Vallejo", "jorge.vallejo@unicauca.edu.co", Rol.REVISOR));
        registrar(new Usuario("revisor3", "Diana Patricia Gómez", "diana.gomez@unicauca.edu.co", Rol.REVISOR));
        registrar(new Usuario("admin", "Coordinación Saber Pro", "saberpro@unicauca.edu.co", Rol.ADMINISTRADOR));
    }

    private void registrar(Usuario usuario) {
        almacen.put(usuario.getLogin().toLowerCase(), usuario);
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        if (login == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(almacen.get(login.toLowerCase()));
    }

    @Override
    public List<Usuario> listarPorRol(Rol rol) {
        List<Usuario> resultado = new ArrayList<>();
        for (Usuario usuario : almacen.values()) {
            if (usuario.tieneRol(rol)) {
                resultado.add(usuario);
            }
        }
        return resultado;
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(almacen.values());
    }
}
