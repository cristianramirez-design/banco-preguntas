package co.edu.unicauca.bancopreguntas.domain.model.estado;

/**
 * Patron GoF State. Cada estado del ciclo de vida de una pregunta decide
 * que transiciones son legales y con que color se representa en la interfaz,
 * evitando condicionales dispersos por el codigo.
 */
public interface IEstadoPregunta {

    String getNombre();

    /** Color hexadecimal con el que la capa de presentacion pinta la etiqueta (HU-2, CA3). */
    String getColorHex();

    /** Descripcion corta usada en la leyenda de estados del prototipo. */
    String getDescripcion();

    /** Indica si la pregunta puede editarse estando en este estado (HU-3, CA4). */
    boolean esEditable();

    /** Transicion HU-2: Borrador -> Pendiente de revision. */
    IEstadoPregunta enviarARevision();

    /** Transicion HU-4: Pendiente de revision -> En revision. */
    IEstadoPregunta asignarRevisores();

    /** Transicion prevista para el siguiente corte: En revision -> Aprobada. */
    IEstadoPregunta aprobar();

    /** Transicion prevista para el siguiente corte: En revision -> Rechazada. */
    IEstadoPregunta rechazar();
}
