package co.edu.unicauca.bancopreguntas.domain.validacion.reglas;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.CampoPregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.ErrorValidacion;
import co.edu.unicauca.bancopreguntas.domain.validacion.IReglaValidacion;

import java.util.Optional;
import java.util.function.Function;

/**
 * Regla reutilizable: un campo de texto debe existir y tener una longitud minima.
 * Se parametriza con una funcion extractora, de modo que la misma clase sirve
 * para contexto, justificacion, bibliografia, competencia, tema y subtema.
 */
public class ReglaCampoObligatorio implements IReglaValidacion {

    private final CampoPregunta campo;
    private final Function<Pregunta, String> extractor;
    private final int longitudMinima;

    public ReglaCampoObligatorio(CampoPregunta campo,
                                 Function<Pregunta, String> extractor,
                                 int longitudMinima) {
        this.campo = campo;
        this.extractor = extractor;
        this.longitudMinima = longitudMinima;
    }

    @Override
    public Optional<ErrorValidacion> validar(Pregunta pregunta) {
        String valor = extractor.apply(pregunta);
        if (valor == null || valor.isBlank()) {
            return Optional.of(new ErrorValidacion(campo,
                    "El campo '" + campo.getEtiqueta() + "' es obligatorio."));
        }
        if (valor.trim().length() < longitudMinima) {
            return Optional.of(new ErrorValidacion(campo,
                    "El campo '" + campo.getEtiqueta() + "' debe tener al menos "
                            + longitudMinima + " caracteres."));
        }
        return Optional.empty();
    }
}
