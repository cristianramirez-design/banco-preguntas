package co.edu.unicauca.bancopreguntas.domain.model;

import java.util.List;

/**
 * Catalogos de clasificacion sugeridos para los desplegables de Competencia,
 * Tema y Subtema del prototipo. Los campos siguen siendo editables porque el
 * autor puede registrar valores nuevos.
 */
public final class Catalogos {

    private Catalogos() {
    }

    public static List<String> competencias() {
        return List.of(
                "Lectura Crítica",
                "Razonamiento Cuantitativo",
                "Competencias Ciudadanas",
                "Comunicación Escrita",
                "Inglés",
                "Ingeniería de Software",
                "Ciencias Naturales");
    }

    public static List<String> temas() {
        return List.of(
                "Comprensión textual",
                "Argumentación",
                "Inferencia textual",
                "Estadística descriptiva",
                "Probabilidad",
                "Interpretación de datos",
                "Constitución política",
                "Diseño de software",
                "Modelos científicos");
    }

    public static List<String> subtemas() {
        return List.of(
                "Idea principal",
                "Identificación de falacias",
                "Medidas de tendencia central",
                "Eventos independientes",
                "Mecanismos de participación",
                "Conectores lógicos",
                "Patrones de diseño",
                "Main idea");
    }
}
