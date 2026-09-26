package co.edu.unicauca.bancopreguntas.util;

import co.edu.unicauca.bancopreguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

import java.util.List;

/** Object Mother: centraliza la construccion de preguntas validas para las pruebas. */
public final class PreguntaMother {

    public static final List<String> OPCIONES = List.of(
            "Conclusion sustentada en la evidencia",
            "Generaliza sin evidencia suficiente",
            "Confunde correlacion con causalidad",
            "Ignora los datos atipicos",
            "Invierte la relacion entre las variables");

    private PreguntaMother() {
    }

    public static Pregunta.Builder valida() {
        return new Pregunta.Builder()
                .contexto("Un docente registra el desempeno de sus estudiantes durante el semestre.")
                .enunciado("Cual es la conclusion mas adecuada segun la evidencia presentada?")
                .opcionesConCorrecta(OPCIONES, 0)
                .justificacion("Es la unica opcion derivada de la evidencia del contexto.")
                .bibliografia("ICFES (2024). Guia Saber Pro.")
                .competencia("Razonamiento Cuantitativo")
                .tema("Estadistica descriptiva")
                .subtema("Medidas de tendencia central")
                .nivelDificultad(NivelDificultad.MEDIA)
                .autorLogin("autor1");
    }
}
