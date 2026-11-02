package co.edu.unicauca.bancopreguntas.revision.domain.politica;

/**
 * Patron GoF Factory Method: crea la politica de decision a partir de un nombre configurable
 * (propiedad revision.politica). Agregar una politica nueva no cambia los servicios (OCP).
 */
public final class PoliticaDecisionFactory {

    private PoliticaDecisionFactory() {
    }

    public static IPoliticaDecision crear(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return new PoliticaUnanimidad();
        }
        return switch (nombre.trim().toUpperCase()) {
            case "UNANIMIDAD" -> new PoliticaUnanimidad();
            case "MAYORIA" -> new PoliticaMayoria();
            default -> throw new IllegalArgumentException("Politica de decision desconocida: " + nombre);
        };
    }
}
