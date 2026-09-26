package co.edu.unicauca.bancopreguntas;

import co.edu.unicauca.bancopreguntas.domain.busqueda.CriterioBusqueda;
import co.edu.unicauca.bancopreguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.servicio.IPreguntaService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Carga preguntas de ejemplo para que la sustentacion muestre paginacion,
 * filtros, los cinco estados con colores y la asignacion de revisores.
 */
public final class DatosDemo {

    private DatosDemo() {
    }

    public static void cargar(IPreguntaService servicio) {
        String[][] clasificacion = {
                {"Razonamiento Cuantitativo", "Estadística descriptiva", "Medidas de tendencia central"},
                {"Razonamiento Cuantitativo", "Probabilidad", "Eventos independientes"},
                {"Lectura Crítica", "Comprensión textual", "Idea principal"},
                {"Lectura Crítica", "Argumentación", "Identificación de falacias"},
                {"Competencias Ciudadanas", "Constitución política", "Mecanismos de participación"},
                {"Inglés", "Comprensión textual", "Main idea"},
                {"Comunicación Escrita", "Comprensión textual", "Conectores lógicos"},
                {"Ingeniería de Software", "Diseño de software", "Patrones de diseño"},
                {"Ciencias Naturales", "Modelos científicos", "Idea principal"},
                {"Razonamiento Cuantitativo", "Interpretación de datos", "Medidas de tendencia central"},
                {"Lectura Crítica", "Inferencia textual", "Idea principal"},
                {"Razonamiento Cuantitativo", "Probabilidad", "Eventos independientes"},
        };

        for (int i = 0; i < clasificacion.length; i++) {
            String autor = i % 3 == 2 ? "autor2" : "autor1";
            Pregunta pregunta = new Pregunta.Builder()
                    .contexto("Un docente analiza el desempeño de un grupo de estudiantes y registra "
                            + "los resultados obtenidos durante el semestre académico, caso " + (i + 1) + ".")
                    .enunciado("De acuerdo con la situación planteada en el caso " + (i + 1)
                            + ", ¿cuál es la conclusión más adecuada?")
                    .opcionesConCorrecta(List.of(
                            "Conclusión sustentada en la evidencia del caso " + (i + 1),
                            "Conclusión que generaliza sin evidencia suficiente " + (i + 1),
                            "Conclusión que confunde correlación con causalidad " + (i + 1),
                            "Conclusión que ignora los datos atípicos del caso " + (i + 1),
                            "Conclusión que invierte la relación entre las variables " + (i + 1)), 0)
                    .justificacion("La opción correcta es la única que se deriva directamente de la "
                            + "evidencia presentada en el contexto, sin agregar supuestos externos.")
                    .bibliografia("ICFES (2024). Guía de orientación Saber Pro. Bogotá.")
                    .competencia(clasificacion[i][0])
                    .tema(clasificacion[i][1])
                    .subtema(clasificacion[i][2])
                    .nivelDificultad(NivelDificultad.values()[i % NivelDificultad.values().length])
                    .autorLogin(autor)
                    .fechaCreacion(LocalDateTime.now().minusDays(clasificacion.length - i))
                    .construir();

            servicio.crear(pregunta);
        }

        // Tres preguntas quedan Pendientes de revision para poder demostrar la HU-4.
        List<Pregunta> deAutor1 = servicio
                .listar(new CriterioBusqueda().autor("autor1"), 1, 20)
                .getContenido();
        for (int i = 0; i < Math.min(3, deAutor1.size()); i++) {
            servicio.enviarARevision(deAutor1.get(i).getId(), "autor1");
        }
    }
}
