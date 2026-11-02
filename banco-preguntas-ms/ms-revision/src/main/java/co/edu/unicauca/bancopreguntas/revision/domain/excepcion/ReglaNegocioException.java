package co.edu.unicauca.bancopreguntas.revision.domain.excepcion;

/** Una operacion viola una regla de negocio de la revision (la API responde 409). */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
