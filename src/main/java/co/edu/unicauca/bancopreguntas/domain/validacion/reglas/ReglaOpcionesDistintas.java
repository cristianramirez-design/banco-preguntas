package co.edu.unicauca.bancopreguntas.domain.validacion.reglas;

import co.edu.unicauca.bancopreguntas.domain.model.Opcion;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.CampoPregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.ErrorValidacion;
import co.edu.unicauca.bancopreguntas.domain.validacion.IReglaValidacion;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/** Ninguna opcion puede repetirse: un distractor igual a la clave invalida el item. */
public class ReglaOpcionesDistintas implements IReglaValidacion {

    @Override
    public Optional<ErrorValidacion> validar(Pregunta pregunta) {
        Set<Opcion> vistas = new HashSet<>();
        for (Opcion opcion : pregunta.getOpciones()) {
            if (!vistas.add(opcion)) {
                return Optional.of(new ErrorValidacion(CampoPregunta.OPCIONES,
                        "Las opciones de respuesta no pueden repetirse: '" + opcion.getTexto() + "'."));
            }
        }
        return Optional.empty();
    }
}
