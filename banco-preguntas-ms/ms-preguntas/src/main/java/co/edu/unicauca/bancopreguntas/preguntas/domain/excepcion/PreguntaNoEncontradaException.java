package co.edu.unicauca.bancopreguntas.preguntas.domain.excepcion;

/** La pregunta solicitada no existe (la API responde 404). */
public class PreguntaNoEncontradaException extends ReglaNegocioException {

    public PreguntaNoEncontradaException(String idPregunta) {
        super("La pregunta no existe: " + idPregunta);
    }
}
