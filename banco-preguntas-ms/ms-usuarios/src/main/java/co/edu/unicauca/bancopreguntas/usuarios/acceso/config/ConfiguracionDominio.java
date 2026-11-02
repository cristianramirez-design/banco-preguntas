package co.edu.unicauca.bancopreguntas.usuarios.acceso.config;

import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.usuarios.domain.model.Usuario;
import co.edu.unicauca.bancopreguntas.usuarios.domain.repositorio.IUsuarioRepositorio;
import co.edu.unicauca.bancopreguntas.usuarios.domain.servicio.UsuarioService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** Composition root de ms-usuarios. */
@Configuration
public class ConfiguracionDominio {

    @Bean
    public UsuarioService usuarioService(IUsuarioRepositorio repositorio) {
        return new UsuarioService(repositorio);
    }

    /** Usuarios de prueba. Hay cuatro revisores para poder demostrar el maximo de 3 (HU-4). */
    @Bean
    public CommandLineRunner cargarUsuarios(UsuarioService servicio) {
        return args -> servicio.cargarIniciales(List.of(
                Usuario.de("autor1", "Ana María Rojas", "ana.rojas@unicauca.edu.co", Rol.AUTOR),
                Usuario.de("autor2", "Carlos Andrés Díaz", "carlos.diaz@unicauca.edu.co", Rol.AUTOR, Rol.REVISOR),
                Usuario.de("revisor1", "Luisa Fernanda Muñoz", "luisa.munoz@unicauca.edu.co", Rol.REVISOR),
                Usuario.de("revisor2", "Jorge Enrique Vallejo", "jorge.vallejo@unicauca.edu.co", Rol.REVISOR),
                Usuario.de("revisor3", "Diana Patricia Gómez", "diana.gomez@unicauca.edu.co", Rol.REVISOR),
                Usuario.de("revisor4", "Mónica Patricia Ruiz", "monica.ruiz@unicauca.edu.co", Rol.REVISOR),
                Usuario.de("admin", "Coordinación Saber Pro", "saberpro@unicauca.edu.co", Rol.ADMINISTRADOR)));
    }

    @Bean
    public OpenAPI documentacionApi() {
        return new OpenAPI()
                // URL relativa: "Try it out" funciona igual desde el gateway (8080) o desde el servicio
                .addServersItem(new Server().url("/").description("Servidor actual"))
                .info(new Info()
                .title("ms-usuarios - Gestion de Usuarios")
                .version("2.0")
                .description("Usuarios, roles e inicio de sesion del Banco de Preguntas Saber Pro."));
    }
}
