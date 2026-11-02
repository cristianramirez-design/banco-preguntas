package co.edu.unicauca.bancopreguntas.preguntas.acceso.config;

import co.edu.unicauca.bancopreguntas.preguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.preguntas.domain.repositorio.IPreguntaRepository;
import co.edu.unicauca.bancopreguntas.preguntas.domain.servicio.IPreguntaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/** Carga 12 preguntas de ejemplo (en Borrador) la primera vez que arranca el microservicio. */
@Component
public class DatosDemo implements CommandLineRunner {

    private final IPreguntaService servicio;
    private final IPreguntaRepository repositorio;

    public DatosDemo(IPreguntaService servicio, IPreguntaRepository repositorio) {
        this.servicio = servicio;
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {
        if (!repositorio.listarTodas().isEmpty()) {
            return;
        }
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
            int caso = i + 1;
            servicio.crear(new Pregunta.Builder()
                    .contexto("Un docente analiza el desempeño de un grupo de estudiantes y registra "
                            + "los resultados obtenidos durante el semestre académico, caso " + caso + ".")
                    .enunciado("De acuerdo con la situación planteada en el caso " + caso
                            + ", ¿cuál es la conclusión más adecuada?")
                    .opcionesConCorrecta(List.of(
                            "Conclusión sustentada en la evidencia del caso " + caso,
                            "Conclusión que generaliza sin evidencia suficiente " + caso,
                            "Conclusión que confunde correlación con causalidad " + caso,
                            "Conclusión que ignora los datos atípicos del caso " + caso,
                            "Conclusión que invierte la relación entre las variables " + caso), 0)
                    .justificacion("La opción correcta es la única que se deriva directamente de la "
                            + "evidencia presentada en el contexto, sin agregar supuestos externos.")
                    .bibliografia("ICFES (2024). Guía de orientación Saber Pro. Bogotá.")
                    .competencia(clasificacion[i][0])
                    .tema(clasificacion[i][1])
                    .subtema(clasificacion[i][2])
                    .nivelDificultad(NivelDificultad.values()[i % NivelDificultad.values().length])
                    .autorLogin(i % 3 == 2 ? "autor2" : "autor1")
                    .fechaCreacion(LocalDateTime.now().minusDays(clasificacion.length - i))
                    .construir());
        }
    }
}
