package co.edu.unicauca.bancopreguntas.usuarios.presentacion;

import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.usuarios.domain.servicio.UsuarioService;
import co.edu.unicauca.bancopreguntas.usuarios.presentacion.dto.LoginRequest;
import co.edu.unicauca.bancopreguntas.usuarios.presentacion.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Usuarios")
public class UsuarioController {

    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/api/usuarios")
    @Operation(summary = "Listar usuarios, opcionalmente por rol (AUTOR, REVISOR, ADMINISTRADOR)")
    public List<UsuarioResponse> listar(@RequestParam(required = false) Rol rol) {
        return servicio.listar(rol).stream().map(UsuarioResponse::de).toList();
    }

    @GetMapping("/api/usuarios/{login}")
    @Operation(summary = "Consultar un usuario (lo usan ms-revision y ms-notificaciones)")
    public UsuarioResponse obtener(@PathVariable String login) {
        return UsuarioResponse.de(servicio.obtener(login));
    }

    @PostMapping("/api/auth/login")
    @Operation(summary = "Iniciar sesion con un usuario existente")
    public UsuarioResponse iniciarSesion(@RequestBody LoginRequest request) {
        return UsuarioResponse.de(servicio.iniciarSesion(request.login()));
    }
}
