package co.edu.unicauca.bancopreguntas.notificaciones.presentacion;

import co.edu.unicauca.bancopreguntas.notificaciones.domain.servicio.NotificacionFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Historial de correos enviados por el sistema")
public class NotificacionController {

    private final NotificacionFacade notificaciones;

    public NotificacionController(NotificacionFacade notificaciones) {
        this.notificaciones = notificaciones;
    }

    @GetMapping
    @Operation(summary = "Correos enviados (mas recientes primero). Filtro opcional por destinatario")
    public List<NotificacionResponse> listar(@RequestParam(required = false) String destinatario) {
        return notificaciones.historial(destinatario).stream().map(NotificacionResponse::de).toList();
    }
}
