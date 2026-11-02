package co.edu.unicauca.bancopreguntas.revision.acceso.cliente;

import co.edu.unicauca.bancopreguntas.revision.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.IDirectorioUsuarios;
import co.edu.unicauca.bancopreguntas.revision.domain.puerto.UsuarioInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * Patron Adapter: implementa el puerto IDirectorioUsuarios consultando la API REST de ms-usuarios.
 * Comunicacion sincrona solo para validar roles en el momento de asignar.
 */
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
        } catch (ResourceAccessException e) {
            throw new ReglaNegocioException("No fue posible consultar el servicio de usuarios. Intente de nuevo.");
        }
    }
}
