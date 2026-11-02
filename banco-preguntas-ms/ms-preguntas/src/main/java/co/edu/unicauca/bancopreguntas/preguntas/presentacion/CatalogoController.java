package co.edu.unicauca.bancopreguntas.preguntas.presentacion;

import co.edu.unicauca.bancopreguntas.preguntas.domain.model.Catalogos;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.estado.IEstadoPregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.validacion.ValidadorFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Listas para los desplegables y la leyenda de estados del frontend. */
@RestController
@Tag(name = "Catalogos")
public class CatalogoController {

    @GetMapping("/api/catalogos")
    @Operation(summary = "Competencias, temas, subtemas, niveles y estados con su color")
    public Map<String, Object> catalogos() {
        List<Map<String, Object>> estados = new ArrayList<>();
        for (IEstadoPregunta estado : EstadosPregunta.valores()) {
            estados.add(Map.of(
                    "nombre", estado.getNombre(),
                    "color", estado.getColorHex(),
                    "descripcion", estado.getDescripcion(),
                    "editable", estado.esEditable()));
        }
        List<Map<String, String>> niveles = new ArrayList<>();
        for (NivelDificultad nivel : NivelDificultad.values()) {
            niveles.add(Map.of("valor", nivel.name(), "etiqueta", nivel.getEtiqueta()));
        }
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("competencias", Catalogos.competencias());
        respuesta.put("temas", Catalogos.temas());
        respuesta.put("subtemas", Catalogos.subtemas());
        respuesta.put("niveles", niveles);
        respuesta.put("estados", estados);
        respuesta.put("opcionesPorPregunta", ValidadorFactory.OPCIONES_POR_PREGUNTA);
        return respuesta;
    }
}
