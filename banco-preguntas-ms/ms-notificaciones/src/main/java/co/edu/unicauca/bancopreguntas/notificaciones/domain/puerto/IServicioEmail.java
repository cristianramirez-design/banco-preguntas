package co.edu.unicauca.bancopreguntas.notificaciones.domain.puerto;

/** Puerto de salida para enviar correos. Lo implementan los adaptadores SMTP y de consola. */
public interface IServicioEmail {

    void enviar(String para, String asunto, String cuerpo);
}
