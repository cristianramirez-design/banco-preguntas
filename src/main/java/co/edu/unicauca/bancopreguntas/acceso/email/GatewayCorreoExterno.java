package co.edu.unicauca.bancopreguntas.acceso.email;

import java.util.Map;

/**
 * Simula una libreria externa de correo con una interfaz que NO coincide
 * con la que necesita el dominio. Sustituir por JavaMail/SMTP real
 * solo implica cambiar el cuerpo de este metodo.
 */
public class GatewayCorreoExterno {

    public void despachar(Map<String, String> sobre) {
        System.out.println("=============== CORREO ENVIADO ===============");
        System.out.println("Para   : " + sobre.get("to"));
        System.out.println("Asunto : " + sobre.get("subject"));
        System.out.println("---------------------------------------------");
        System.out.println(sobre.get("body"));
        System.out.println("==============================================");
    }
}
