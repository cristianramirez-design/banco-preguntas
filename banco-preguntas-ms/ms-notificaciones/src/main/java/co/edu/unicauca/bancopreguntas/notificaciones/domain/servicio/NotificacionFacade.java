package co.edu.unicauca.bancopreguntas.notificaciones.domain.servicio;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.evento.PreguntaEvaluadaEvento;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.evento.RevisoresAsignadosEvento;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.model.Mensaje;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.model.Notificacion;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.plantilla.PlantillaNotificacion;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.plantilla.PlantillaPreguntaEvaluada;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.plantilla.PlantillaRevisoresAsignados;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.IDirectorioUsuarios;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.INotificacionRepositorio;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.IServicioEmail;
import co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto.UsuarioInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Patron Facade: una sola entrada simple ("notificar este evento") que esconde el subsistema de
 * plantillas, el directorio de usuarios, el envio de correo y el historial. Un correo que falla
 * no detiene a los demas: queda registrado como FALLIDA.
 */
public class NotificacionFacade {

    private final PlantillaNotificacion<RevisoresAsignadosEvento> plantillaAsignacion;
    private final PlantillaNotificacion<PreguntaEvaluadaEvento> plantillaEvaluacion;
    private final IDirectorioUsuarios directorio;
    private final IServicioEmail email;
    private final INotificacionRepositorio historial;

    public NotificacionFacade(IDirectorioUsuarios directorio, IServicioEmail email, INotificacionRepositorio historial) {
        this.plantillaAsignacion = new PlantillaRevisoresAsignados();
        this.plantillaEvaluacion = new PlantillaPreguntaEvaluada();
        this.directorio = directorio;
        this.email = email;
        this.historial = historial;
    }

    public List<Notificacion> notificarRevisoresAsignados(RevisoresAsignadosEvento evento) {
        return enviarTodos(plantillaAsignacion.generar(evento));
    }

    public List<Notificacion> notificarPreguntaEvaluada(PreguntaEvaluadaEvento evento) {
        return enviarTodos(plantillaEvaluacion.generar(evento));
    }

    public List<Notificacion> historial(String destinatarioLogin) {
        return historial.listar(destinatarioLogin);
    }

    private List<Notificacion> enviarTodos(List<Mensaje> mensajes) {
        List<Notificacion> resultado = new ArrayList<>();
        for (Mensaje m : mensajes) {
            resultado.add(historial.guardar(enviar(m)));
        }
        return resultado;
    }

    private Notificacion enviar(Mensaje m) {
        Optional<UsuarioInfo> usuario;
        try {
            usuario = directorio.buscar(m.destinatarioLogin());
        } catch (RuntimeException e) {
            return Notificacion.fallida(m, null, "No se pudo consultar el usuario: " + e.getMessage());
        }
        String correo = usuario.map(UsuarioInfo::email).orElse(null);
        if (correo == null || correo.isBlank()) {
            return Notificacion.fallida(m, null, "El usuario no tiene correo registrado");
        }
        try {
            email.enviar(correo, m.asunto(), m.cuerpo());
            return Notificacion.enviada(m, correo);
        } catch (RuntimeException e) {
            return Notificacion.fallida(m, correo, e.getMessage());
        }
    }
}
