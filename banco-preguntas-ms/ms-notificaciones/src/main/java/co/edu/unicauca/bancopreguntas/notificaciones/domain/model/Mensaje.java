package co.edu.unicauca.bancopreguntas.notificaciones.domain.model;

/** Mensaje armado por una plantilla, antes de resolver el correo del destinatario. */
public record Mensaje(String destinatarioLogin, String asunto, String cuerpo, String tipo) {
}
