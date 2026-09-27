package co.edu.unicauca.bancopreguntas.domain.model;

public enum Rol {
    AUTOR("Autor de preguntas"),
    REVISOR("Revisor"),
    ADMINISTRADOR("Administrador");

    private final String etiqueta;

    Rol(String etiqueta) {
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
