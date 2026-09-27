package co.edu.unicauca.bancopreguntas.domain.validacion;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.reglas.ReglaCampoObligatorio;
import co.edu.unicauca.bancopreguntas.domain.validacion.reglas.ReglaEnunciadoInterrogativo;
import co.edu.unicauca.bancopreguntas.domain.validacion.reglas.ReglaNivelDificultad;
import co.edu.unicauca.bancopreguntas.domain.validacion.reglas.ReglaNumeroOpciones;
import co.edu.unicauca.bancopreguntas.domain.validacion.reglas.ReglaOpcionesDistintas;
import co.edu.unicauca.bancopreguntas.domain.validacion.reglas.ReglaRespuestaCorrectaUnica;

/**
 * Patron GoF Factory Method: centraliza el armado del validador estructural.
 *
 * OPCIONES_POR_PREGUNTA es el unico punto donde se decide cuantas alternativas
 * tiene el item. Con valor 5 el item cumple literalmente el enunciado del primer
 * corte: cuatro distractores MAS la respuesta correcta. Si el cliente confirma que
 * el prototipo manda (cuatro alternativas en total, una marcada como correcta), se
 * cambia a 4 aqui: el formulario se ajusta solo y ninguna otra clase se modifica.
 */
public final class ValidadorFactory {

    public static final int OPCIONES_POR_PREGUNTA = 5;

    private ValidadorFactory() {
    }

    public static IValidadorPregunta porDefecto() {
        return new ValidadorEstructural()
                .agregar(new ReglaCampoObligatorio(CampoPregunta.CONTEXTO, Pregunta::getContexto, 20))
                .agregar(new ReglaEnunciadoInterrogativo())
                .agregar(new ReglaNumeroOpciones(OPCIONES_POR_PREGUNTA))
                .agregar(new ReglaRespuestaCorrectaUnica())
                .agregar(new ReglaOpcionesDistintas())
                .agregar(new ReglaCampoObligatorio(CampoPregunta.JUSTIFICACION, Pregunta::getJustificacion, 15))
                .agregar(new ReglaCampoObligatorio(CampoPregunta.BIBLIOGRAFIA, Pregunta::getBibliografia, 5))
                .agregar(new ReglaCampoObligatorio(CampoPregunta.COMPETENCIA, Pregunta::getCompetencia, 3))
                .agregar(new ReglaCampoObligatorio(CampoPregunta.TEMA, Pregunta::getTema, 3))
                .agregar(new ReglaCampoObligatorio(CampoPregunta.SUBTEMA, Pregunta::getSubtema, 3))
                .agregar(new ReglaNivelDificultad());
    }
}
