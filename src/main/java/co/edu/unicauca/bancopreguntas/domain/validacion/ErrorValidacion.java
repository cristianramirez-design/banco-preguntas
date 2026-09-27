package co.edu.unicauca.bancopreguntas.domain.validacion;

/** Error estructural: el campo afectado y el mensaje que ve el usuario. */
public class ErrorValidacion {

    private final CampoPregunta campo;
    private final String mensaje;

    public ErrorValidacion(CampoPregunta campo, String mensaje) {
        this.campo = campo;
        this.mensaje = mensaje;
    }

    public CampoPregunta getCampo() {
        return campo;
    }

    public String getMensaje() {
        return mensaje;
    }

    @Override
    public String toString() {
        return mensaje;
    }
}
