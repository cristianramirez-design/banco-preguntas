package co.edu.unicauca.bancopreguntas.acceso.memoria;

import co.edu.unicauca.bancopreguntas.domain.servicio.IGeneradorCodigo;

import java.util.concurrent.atomic.AtomicInteger;

/** Genera codigos legibles con el formato A-0231 usado en los prototipos. */
public class GeneradorCodigoSecuencial implements IGeneradorCodigo {

    private final String prefijo;
    private final AtomicInteger contador;

    public GeneradorCodigoSecuencial() {
        this("A", 227);
    }

    public GeneradorCodigoSecuencial(String prefijo, int inicio) {
        this.prefijo = prefijo;
        this.contador = new AtomicInteger(inicio);
    }

    @Override
    public String siguiente() {
        return String.format("%s-%04d", prefijo, contador.getAndIncrement());
    }
}
