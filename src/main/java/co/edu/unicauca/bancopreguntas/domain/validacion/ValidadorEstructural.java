package co.edu.unicauca.bancopreguntas.domain.validacion;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

import java.util.ArrayList;
import java.util.List;

/**
 * Patron GoF Composite: agrupa varias reglas (Strategy) y las trata
 * como si fueran una sola regla de validacion.
 */
public class ValidadorEstructural implements IValidadorPregunta {

    private final List<IReglaValidacion> reglas = new ArrayList<>();

    public ValidadorEstructural agregar(IReglaValidacion regla) {
        reglas.add(regla);
        return this;
    }

    @Override
    public ResultadoValidacion validar(Pregunta pregunta) {
        ResultadoValidacion resultado = new ResultadoValidacion();
        for (IReglaValidacion regla : reglas) {
            regla.validar(pregunta).ifPresent(resultado::agregarError);
        }
        return resultado;
    }

    public int cantidadReglas() {
        return reglas.size();
    }
}
