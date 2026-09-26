package co.edu.unicauca.bancopreguntas.domain.notificacion;

/** Puerto de salida hacia el servicio de correo. */
public interface IServicioEmail {

    void enviar(String destinatario, String asunto, String cuerpo);
}
