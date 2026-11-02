package co.edu.unicauca.bancopreguntas.revision.presentacion;

import co.edu.unicauca.bancopreguntas.revision.domain.servicio.AsignacionService;
import co.edu.unicauca.bancopreguntas.revision.domain.servicio.EvaluacionService;
import co.edu.unicauca.bancopreguntas.revision.presentacion.dto.AsignacionRequest;
import co.edu.unicauca.bancopreguntas.revision.presentacion.dto.EvaluacionRequest;
import co.edu.unicauca.bancopreguntas.revision.presentacion.dto.PendienteResponse;
import co.edu.unicauca.bancopreguntas.revision.presentacion.dto.RevisionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/revisiones")
@Tag(name = "Revisiones", description = "HU-4 asignar revisores y HU-5 evaluar preguntas")
public class RevisionController {

    private final AsignacionService asignacion;
    private final EvaluacionService evaluacion;

    public RevisionController(AsignacionService asignacion, EvaluacionService evaluacion) {
        this.asignacion = asignacion;
        this.evaluacion = evaluacion;
    }

    @GetMapping("/pendientes")
    @Operation(summary = "HU-4: preguntas enviadas a revision que aun no tienen revisores")
    public List<PendienteResponse> pendientes() {
        return asignacion.listarPendientes().stream().map(PendienteResponse::de).toList();
    }

    @PostMapping("/asignaciones")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "HU-4: asignar de 1 a 3 revisores a una pregunta (publica RevisoresAsignados)")
    public List<RevisionResponse> asignar(@Valid @RequestBody AsignacionRequest req) {
        List<String> revisores = req.revisores() == null ? List.of() : req.revisores();
        return asignacion.asignar(req.preguntaId(), revisores, req.administrador()).stream()
                .map(RevisionResponse::de).toList();
    }

    @GetMapping("/mias")
    @Operation(summary = "HU-5: revisiones asignadas al revisor del encabezado X-Usuario")
    public List<RevisionResponse> mias(@RequestHeader("X-Usuario") String revisor) {
        return evaluacion.listarAsignadas(revisor.trim().toLowerCase()).stream().map(RevisionResponse::de).toList();
    }

    @PostMapping("/{id}/evaluacion")
    @Operation(summary = "HU-5: aprobar o rechazar (rechazar exige observaciones)")
    public RevisionResponse evaluar(@PathVariable String id, @RequestHeader("X-Usuario") String revisor,
                                    @Valid @RequestBody EvaluacionRequest req) {
        return RevisionResponse.de(evaluacion.evaluar(id, revisor.trim().toLowerCase(), req.decision(),
                req.observaciones()));
    }

    @GetMapping("/pregunta/{preguntaId}")
    @Operation(summary = "HU-5: observaciones de los revisores sobre una pregunta (las ve el autor)")
    public List<RevisionResponse> dePregunta(@PathVariable String preguntaId) {
        return evaluacion.observacionesDePregunta(preguntaId).stream().map(RevisionResponse::de).toList();
    }
}
