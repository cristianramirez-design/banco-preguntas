package co.edu.unicauca.bancopreguntas.preguntas.acceso.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Opcion de respuesta tal como se guarda en la tabla pregunta_opciones. */
@Embeddable
public class OpcionEmbeddable {

    @Column(length = 1000)
    private String texto;

    private boolean correcta;

    protected OpcionEmbeddable() {
    }

    public OpcionEmbeddable(String texto, boolean correcta) {
        this.texto = texto;
        this.correcta = correcta;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isCorrecta() {
        return correcta;
    }
}
