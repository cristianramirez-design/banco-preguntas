package co.edu.unicauca.bancopreguntas.domain;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.CampoPregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.IValidadorPregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.ResultadoValidacion;
import co.edu.unicauca.bancopreguntas.domain.validacion.ValidadorFactory;
import co.edu.unicauca.bancopreguntas.util.PreguntaMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidadorEstructuralTest {

    private IValidadorPregunta validador;

    @BeforeEach
    void inicializar() {
        validador = ValidadorFactory.porDefecto();
    }

    @Test
    @DisplayName("Una pregunta completa pasa la validacion estructural")
    void preguntaValida() {
        ResultadoValidacion resultado = validador.validar(PreguntaMother.valida().construir());
        assertTrue(resultado.esValido(), () -> "Errores: " + resultado.getMensajes());
    }

    @Test
    @DisplayName("El contexto es obligatorio y el error apunta al campo Contexto")
    void contextoObligatorio() {
        ResultadoValidacion resultado = validador.validar(
                PreguntaMother.valida().contexto("").construir());

        assertFalse(resultado.esValido());
        assertTrue(resultado.getCamposInvalidos().contains(CampoPregunta.CONTEXTO));
    }

    @Test
    @DisplayName("La pregunta directa debe terminar en signo de interrogacion")
    void enunciadoInterrogativo() {
        ResultadoValidacion resultado = validador.validar(
                PreguntaMother.valida().enunciado("Seleccione la conclusion mas adecuada").construir());

        assertTrue(resultado.getCamposInvalidos().contains(CampoPregunta.ENUNCIADO));
        assertTrue(contieneError(resultado.getMensajes(), "?"));
    }

    @Test
    @DisplayName("Deben existir exactamente cinco opciones: cuatro distractores y la correcta")
    void cincoOpciones() {
        ResultadoValidacion resultado = validador.validar(
                PreguntaMother.valida().opcionesConCorrecta(List.of("Uno", "Dos"), 0).construir());

        assertTrue(resultado.getCamposInvalidos().contains(CampoPregunta.OPCIONES));
        assertTrue(contieneError(resultado.getMensajes(), "exactamente 5 opciones"));
    }

    @Test
    @DisplayName("Ninguna opcion de respuesta puede quedar vacia")
    void opcionesNoVacias() {
        ResultadoValidacion resultado = validador.validar(PreguntaMother.valida()
                .opcionesConCorrecta(List.of("Correcta", "Dos", "   ", "Cuatro", "Cinco"), 0).construir());

        assertTrue(contieneError(resultado.getMensajes(), "puede estar vacia"));
    }

    @Test
    @DisplayName("Debe marcarse cual opcion es la respuesta correcta")
    void respuestaCorrectaObligatoria() {
        ResultadoValidacion resultado = validador.validar(PreguntaMother.valida()
                .opcionesConCorrecta(PreguntaMother.OPCIONES, -1).construir());

        assertTrue(resultado.getCamposInvalidos().contains(CampoPregunta.RESPUESTA_CORRECTA));
        assertTrue(contieneError(resultado.getMensajes(), "marcar"));
    }

    @Test
    @DisplayName("Seleccion multiple con UNICA respuesta: no se admiten dos correctas")
    void respuestaCorrectaUnica() {
        Pregunta pregunta = PreguntaMother.valida()
                .opciones(List.of(
                        co.edu.unicauca.bancopreguntas.domain.model.Opcion.correcta("Una"),
                        co.edu.unicauca.bancopreguntas.domain.model.Opcion.correcta("Otra"),
                        co.edu.unicauca.bancopreguntas.domain.model.Opcion.distractor("Tres"),
                        co.edu.unicauca.bancopreguntas.domain.model.Opcion.distractor("Cuatro"),
                        co.edu.unicauca.bancopreguntas.domain.model.Opcion.distractor("Cinco")))
                .construir();

        assertTrue(contieneError(validador.validar(pregunta).getMensajes(), "una respuesta correcta"));
    }

    @Test
    @DisplayName("Una opcion no puede repetir el texto de otra")
    void opcionesNoSeRepiten() {
        ResultadoValidacion resultado = validador.validar(PreguntaMother.valida()
                .opcionesConCorrecta(List.of("Igual", "igual", "Tres", "Cuatro", "Cinco"), 0).construir());

        assertTrue(contieneError(resultado.getMensajes(), "repetirse"));
    }

    @Test
    @DisplayName("El nivel de dificultad es obligatorio")
    void nivelDificultadObligatorio() {
        ResultadoValidacion resultado = validador.validar(
                PreguntaMother.valida().nivelDificultad(null).construir());

        assertTrue(resultado.getCamposInvalidos().contains(CampoPregunta.NIVEL_DIFICULTAD));
    }

    @Test
    @DisplayName("Subtema y competencia tambien son obligatorios")
    void clasificacionObligatoria() {
        ResultadoValidacion resultado = validador.validar(
                PreguntaMother.valida().subtema("").competencia("").construir());

        assertTrue(resultado.getCamposInvalidos().contains(CampoPregunta.SUBTEMA));
        assertTrue(resultado.getCamposInvalidos().contains(CampoPregunta.COMPETENCIA));
    }

    @Test
    @DisplayName("Una pregunta vacia acumula varios errores y varios campos resaltados")
    void acumulaErrores() {
        ResultadoValidacion resultado = validador.validar(new Pregunta.Builder().construir());

        assertFalse(resultado.esValido());
        assertTrue(resultado.getMensajes().size() >= 8);
        assertTrue(resultado.getCamposInvalidos().size() >= 8);
    }

    @Test
    @DisplayName("El item se configura con cinco opciones de respuesta")
    void cantidadDeOpcionesConfigurada() {
        assertEquals(5, ValidadorFactory.OPCIONES_POR_PREGUNTA);
    }

    private boolean contieneError(List<String> errores, String fragmento) {
        return errores.stream().anyMatch(e -> e.toLowerCase().contains(fragmento.toLowerCase()));
    }
}
