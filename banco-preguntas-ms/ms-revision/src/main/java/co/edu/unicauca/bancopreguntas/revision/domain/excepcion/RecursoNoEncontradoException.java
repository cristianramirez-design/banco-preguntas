package co.edu.unicauca.bancopreguntas.revision.domain.excepcion;

/** No existe la pregunta pendiente o la revision solicitada (la API responde 404). */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
