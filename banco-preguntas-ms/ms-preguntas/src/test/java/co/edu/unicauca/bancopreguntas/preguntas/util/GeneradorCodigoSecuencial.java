package co.edu.unicauca.bancopreguntas.preguntas.util;

import co.edu.unicauca.bancopreguntas.preguntas.domain.servicio.IGeneradorCodigo;

/** Doble de prueba: genera A-0227, A-0228, ... */
public class GeneradorCodigoSecuencial implements IGeneradorCodigo {

    private int siguiente;

    public GeneradorCodigoSecuencial(int inicio) {
        this.siguiente = inicio;
    }

    @Override
    public String siguiente() {
        return String.format("A-%04d", siguiente++);
    }
}
