package co.edu.unicauca.bancopreguntas.preguntas.presentacion;

import co.edu.unicauca.bancopreguntas.preguntas.domain.busqueda.CriterioBusqueda;
import co.edu.unicauca.bancopreguntas.preguntas.domain.busqueda.PaginaResultado;
import co.edu.unicauca.bancopreguntas.preguntas.domain.excepcion.PreguntaNoEncontradaException;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.servicio.IPreguntaService;
import co.edu.unicauca.bancopreguntas.preguntas.presentacion.dto.PaginaResponse;
import co.edu.unicauca.bancopreguntas.preguntas.presentacion.dto.PreguntaRequest;
import co.edu.unicauca.bancopreguntas.preguntas.presentacion.dto.PreguntaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API REST de la Gestion de Preguntas. El usuario que hace la peticion llega en el
 * encabezado X-Usuario (lo pone el frontend despues de iniciar sesion).
 */
@RestController
@RequestMapping("/api/preguntas")
@Tag(name = "Preguntas", description = "HU-1 crear, HU-2 enviar a revision, HU-3 listar y editar")
public class PreguntaController {

    private final IPreguntaService servicio;

    public PreguntaController(IPreguntaService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "HU-1: crear una pregunta (aplica la validacion estructural)")
    public PreguntaResponse crear(@RequestBody PreguntaRequest request,
                                  @Parameter(description = "Login del autor") @RequestHeader("X-Usuario") String usuario) {
        Pregunta pregunta = PreguntaMapper.aBuilder(request).autorLogin(usuario).construir();
        return PreguntaMapper.aResponse(servicio.crear(pregunta));
    }

    @PutMapping("/{id}")
    @Operation(summary = "HU-3 CA4: editar una pregunta en Borrador o Rechazada")
    public PreguntaResponse actualizar(@PathVariable String id,
                                       @RequestBody PreguntaRequest request,
                                       @RequestHeader("X-Usuario") String usuario) {
        Pregunta cambios = PreguntaMapper.aBuilder(request).id(id).construir();
        return PreguntaMapper.aResponse(servicio.actualizar(cambios, usuario));
    }

    @GetMapping
    @Operation(summary = "HU-3: listar preguntas con filtros y paginacion")
    public PaginaResponse<PreguntaResponse> listar(
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String tema,
            @RequestParam(required = false) String subtema,
            @RequestParam(required = false) String competencia,
            @RequestParam(required = false) String nivel,
            @RequestParam(required = false) String texto,
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {

        CriterioBusqueda criterio = new CriterioBusqueda()
                .autor(autor).tema(tema).subtema(subtema).competencia(competencia).texto(texto);
        if (estado != null && !estado.isBlank()) {
            criterio.estado(EstadosPregunta.porNombre(estado));
        }
        if (nivel != null && !nivel.isBlank()) {
            criterio.nivelDificultad(NivelDificultad.valueOf(nivel.trim().toUpperCase()));
        }

        PaginaResultado<Pregunta> resultado = servicio.listar(criterio, pagina, tamanio);
        List<PreguntaResponse> contenido = resultado.getContenido().stream()
                .map(PreguntaMapper::aResponse).toList();
        return new PaginaResponse<>(contenido, resultado.getPaginaActual(), resultado.getTamanioPagina(),
                resultado.getTotalElementos(), resultado.getTotalPaginas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver el detalle de una pregunta")
    public PreguntaResponse obtener(@PathVariable String id) {
        return servicio.buscarPorId(id)
                .map(PreguntaMapper::aResponse)
                .orElseThrow(() -> new PreguntaNoEncontradaException(id));
    }

    @PatchMapping("/{id}/enviar-revision")
    @Operation(summary = "HU-2: cambiar de Borrador a Pendiente de revision (publica un evento)")
    public PreguntaResponse enviarARevision(@PathVariable String id,
                                            @RequestHeader("X-Usuario") String usuario) {
        return PreguntaMapper.aResponse(servicio.enviarARevision(id, usuario));
    }
}
