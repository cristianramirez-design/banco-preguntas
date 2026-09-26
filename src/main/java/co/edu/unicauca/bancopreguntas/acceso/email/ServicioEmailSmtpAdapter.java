package co.edu.unicauca.bancopreguntas.acceso.email;

import co.edu.unicauca.bancopreguntas.domain.notificacion.IServicioEmail;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Patron GoF Adapter: adapta la interfaz del gateway externo (despachar(Map))
 * a la interfaz que el dominio espera (enviar(destinatario, asunto, cuerpo)).
 */
public class ServicioEmailSmtpAdapter implements IServicioEmail {

    private final GatewayCorreoExterno gateway;

    public ServicioEmailSmtpAdapter(GatewayCorreoExterno gateway) {
        this.gateway = gateway;
    }

    @Override
    public void enviar(String destinatario, String asunto, String cuerpo) {
        Map<String, String> sobre = new LinkedHashMap<>();
        sobre.put("to", destinatario);
        sobre.put("subject", asunto);
        sobre.put("body", cuerpo);
        gateway.despachar(sobre);
    }
}
