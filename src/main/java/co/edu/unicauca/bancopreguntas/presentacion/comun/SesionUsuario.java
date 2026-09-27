package co.edu.unicauca.bancopreguntas.presentacion.comun;

import co.edu.unicauca.bancopreguntas.domain.model.Usuario;

/**
 * Patron GoF Singleton: mantiene el usuario autenticado de la sesion.
 * Para el primer corte el cambio de usuario se hace desde la barra superior,
 * lo que permite demostrar los roles de autor y administrador en la sustentacion.
 */
public class SesionUsuario {

    private static final SesionUsuario INSTANCIA = new SesionUsuario();

    private Usuario usuarioActual;

    private SesionUsuario() {
    }

    public static SesionUsuario getInstancia() {
        return INSTANCIA;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public String getLogin() {
        return usuarioActual == null ? "" : usuarioActual.getLogin();
    }
}
