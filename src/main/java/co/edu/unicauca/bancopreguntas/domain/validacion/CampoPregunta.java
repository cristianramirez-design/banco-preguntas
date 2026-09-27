package co.edu.unicauca.bancopreguntas.domain.validacion;

/**
 * Campos del formulario de la HU-1. El validador devuelve el campo afectado
 * para que la vista pueda resaltarlo en rojo (HU-1, CA2).
 */
public enum CampoPregunta {

    CONTEXTO("Contexto"),
    ENUNCIADO("Pregunta directa"),
    OPCIONES("Opciones de respuesta"),
    RESPUESTA_CORRECTA("Respuesta correcta"),
    JUSTIFICACION("Justificacion de la respuesta"),
    BIBLIOGRAFIA("Bibliografia"),
    COMPETENCIA("Competencia"),
    TEMA("Tema"),
    SUBTEMA("Subtema"),
    NIVEL_DIFICULTAD("Nivel de dificultad");

    private final String etiqueta;

    CampoPregunta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
