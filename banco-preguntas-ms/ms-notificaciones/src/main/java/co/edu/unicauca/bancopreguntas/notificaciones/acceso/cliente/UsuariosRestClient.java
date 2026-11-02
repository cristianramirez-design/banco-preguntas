package co.edu.unicauca.bancopreguntas.notificaciones.acceso.cliente;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.IDirectorioUsuarios;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.UsuarioInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/** Patron Adapter: obtiene el correo de cada destinatario desde ms-usuarios. */
@Component
public class UsuariosRestClient implements IDirectorioUsuarios {

    private final RestClient cliente;

    public UsuariosRestClient(RestClient.Builder builder, @Value("${ms.usuarios.url}") String urlBase) {
        this.cliente = builder.baseUrl(urlBase).build();
    }

    @Override
    public Optional<UsuarioInfo> buscar(String login) {
        try {
            return Optional.ofNullable(cliente.get().uri("/api/usuarios/{login}", login)
                    .retrieve().body(UsuarioInfo.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }
}
