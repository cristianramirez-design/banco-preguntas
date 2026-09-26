package co.edu.unicauca.bancopreguntas.acceso.sqlite;

/** Envuelve las SQLException para que no se filtren fuera de la capa de datos. */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
