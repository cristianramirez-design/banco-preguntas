package co.edu.unicauca.bancopreguntas.domain.validacion.reglas;

import co.edu.unicauca.bancopreguntas.domain.model.Opcion;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.CampoPregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.ErrorValidacion;
import co.edu.unicauca.bancopreguntas.domain.validacion.IReglaValidacion;

import java.util.Optional;

/**
 * La pregunta debe tener exactamente N opciones de respuesta, ninguna vacia.
 * La cantidad se parametriza: cambiar de cuatro a cinco opciones es un cambio
 * de un solo valor en ValidadorFactory (principio Open/Closed).
 */
public class ReglaNumeroOpciones implements IReglaValidacion {

    private final int cantidadEsperada;

    public ReglaNumeroOpciones(int cantidadEsperada) {
        this.cantidadEsperada = cantidadEsperada;
    }

    @Override
    public Optional<ErrorValidacion> validar(Pregunta pregunta) {
        if (pregunta.getOpciones().size() != cantidadEsperada) {
            return Optional.of(new ErrorValidacion(CampoPregunta.OPCIONES,
                    "La pregunta debe tener exactamente " + cantidadEsperada
                            + " opciones de respuesta (tiene " + pregunta.getOpciones().size() + ")."));
        }
        for (Opcion opcion : pregunta.getOpciones()) {
            if (opcion.estaVacia()) {
                return Optional.of(new ErrorValidacion(CampoPregunta.OPCIONES,
                        "Ninguna opcion de respuesta puede estar vacia."));
            }
        }
        return Optional.empty();
    }
}
