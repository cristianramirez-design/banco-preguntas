package co.edu.unicauca.bancopreguntas.notificaciones.acceso.persistencia;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.model.Notificacion;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.INotificacionRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Historial en memoria: las notificaciones son un registro de apoyo (el correo real ya salio),
 * por eso este microservicio no necesita base de datos. Guarda las ultimas 500.
 */
@Repository
public class NotificacionRepositorioMemoria implements INotificacionRepositorio {

    private static final int MAXIMO = 500;
    private final ConcurrentLinkedDeque<Notificacion> datos = new ConcurrentLinkedDeque<>();

    @Override
    public Notificacion guardar(Notificacion n) {
        datos.addFirst(n);
        while (datos.size() > MAXIMO) {
            datos.pollLast();
        }
        return n;
    }

    @Override
    public List<Notificacion> listar(String destinatarioLogin) {
        return datos.stream()
                .filter(n -> destinatarioLogin == null || destinatarioLogin.equalsIgnoreCase(n.getDestinatarioLogin()))
                .sorted(Comparator.comparing(Notificacion::getFecha).reversed())
                .toList();
    }
}
