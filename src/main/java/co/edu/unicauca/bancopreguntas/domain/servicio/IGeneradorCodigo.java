package co.edu.unicauca.bancopreguntas.domain.servicio;

/**
 * Puerto para generar el identificador legible de la pregunta (#A-0231),
 * que las tablas de la HU-3 y la HU-4 muestran en la columna ID.
 */
public interface IGeneradorCodigo {

    String siguiente();
}
