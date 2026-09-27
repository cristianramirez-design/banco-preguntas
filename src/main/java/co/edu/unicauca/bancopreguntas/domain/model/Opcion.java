package co.edu.unicauca.bancopreguntas.domain.model;

import java.util.Objects;

/**
 * Opcion de respuesta de una pregunta de seleccion multiple.
 * Una de las cuatro opciones queda marcada como la respuesta correcta,
 * tal como lo muestra el prototipo de la HU-1.
 */
public class Opcion {

    private final String texto;
    private final boolean correcta;

    private Opcion(String texto, boolean correcta) {
        this.texto = texto == null ? "" : texto.trim();
        this.correcta = correcta;
    }

    public static Opcion nueva(String texto, boolean correcta) {
        return new Opcion(texto, correcta);
    }

    public static Opcion correcta(String texto) {
        return new Opcion(texto, true);
    }

    public static Opcion distractor(String texto) {
        return new Opcion(texto, false);
    }

    public String getTexto() {
        return texto;
    }

    public boolean esCorrecta() {
        return correcta;
    }

    public boolean estaVacia() {
        return texto.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Opcion)) {
            return false;
        }
        return texto.equalsIgnoreCase(((Opcion) o).texto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texto.toLowerCase());
    }

    @Override
    public String toString() {
        return texto;
    }
}
