package co.edu.unicauca.bancopreguntas.domain.model;

public enum NivelDificultad {
    BAJA("Baja"),
    MEDIA("Media"),
    ALTA("Alta");

    private final String etiqueta;

    NivelDificultad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
