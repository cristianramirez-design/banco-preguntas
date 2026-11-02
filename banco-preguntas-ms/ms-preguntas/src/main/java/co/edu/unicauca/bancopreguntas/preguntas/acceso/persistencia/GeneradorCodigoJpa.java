package co.edu.unicauca.bancopreguntas.preguntas.acceso.persistencia;

import co.edu.unicauca.bancopreguntas.preguntas.domain.servicio.IGeneradorCodigo;
import org.springframework.stereotype.Component;

/**
 * Genera el siguiente codigo A-NNNN a partir del mayor codigo guardado, para que
 * los codigos no se repitan entre reinicios del microservicio.
 */
@Component
public class GeneradorCodigoJpa implements IGeneradorCodigo {

    private static final int INICIO = 227;

    private final PreguntaJpaRepository jpa;

    public GeneradorCodigoJpa(PreguntaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public synchronized String siguiente() {
        String mayor = jpa.buscarMayorCodigo();
        int siguiente = INICIO;
        if (mayor != null && mayor.startsWith("A-")) {
            try {
                siguiente = Math.max(INICIO, Integer.parseInt(mayor.substring(2)) + 1);
            } catch (NumberFormatException ignorada) {
                // se conserva el valor inicial
            }
        }
        return String.format("A-%04d", siguiente);
    }
}
