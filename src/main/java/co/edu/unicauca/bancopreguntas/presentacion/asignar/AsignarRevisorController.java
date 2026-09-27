package co.edu.unicauca.bancopreguntas.presentacion.asignar;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.domain.model.Usuario;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IUsuarioRepository;
import co.edu.unicauca.bancopreguntas.domain.servicio.IAsignacionService;
import co.edu.unicauca.bancopreguntas.presentacion.comun.SesionUsuario;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/** Controlador MVC de la HU-4. */
public class AsignarRevisorController {

    private final AsignarRevisorView vista;
    private final IAsignacionService asignacionService;
    private final IUsuarioRepository usuarioRepository;

    public AsignarRevisorController(AsignarRevisorView vista,
                                    IAsignacionService asignacionService,
                                    IUsuarioRepository usuarioRepository) {
        this.vista = vista;
        this.asignacionService = asignacionService;
        this.usuarioRepository = usuarioRepository;
        vista.getTableModel().setResolverNombreAutor(this::nombreDe);
        registrarEventos();
    }

    private String nombreDe(String login) {
        return usuarioRepository.buscarPorLogin(login)
                .map(Usuario::getNombre)
                .orElse(login);
    }

    private void registrarEventos() {
        vista.getBtnRefrescar().addActionListener(e -> refrescar());
        vista.getBtnAsignar().addActionListener(e -> abrirModalAsignacion());
        vista.getTabla().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                if (evento.getClickCount() == 2 && vista.getBtnAsignar().isEnabled()) {
                    abrirModalAsignacion();
                }
            }
        });
    }

    public void refrescar() {
        List<Pregunta> pendientes = asignacionService.listarPendientesDeRevision();
        vista.getTableModel().setPreguntas(pendientes);

        Usuario usuario = SesionUsuario.getInstancia().getUsuarioActual();
        boolean esAdministrador = usuario != null && usuario.tieneRol(Rol.ADMINISTRADOR);
        vista.getBtnAsignar().setEnabled(esAdministrador);
        vista.getLblEstado().setText(esAdministrador
                ? pendientes.size() + " pregunta(s) pendiente(s)   "
                : "Inicie sesion como administrador para asignar revisores   ");
    }

    private void abrirModalAsignacion() {
        int fila = vista.getTabla().getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione una pregunta pendiente de revision.",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pregunta pregunta = vista.getTableModel().getPreguntaEn(fila);
        List<Usuario> disponibles = asignacionService.listarRevisoresDisponibles(pregunta.getId());

        Frame propietario = (Frame) SwingUtilities.getWindowAncestor(vista);
        AsignarRevisoresDialog dialogo = new AsignarRevisoresDialog(propietario, pregunta, disponibles);
        dialogo.setVisible(true);

        if (!dialogo.fueConfirmado()) {
            return;
        }

        List<Usuario> seleccionados = dialogo.getRevisoresSeleccionados();
        if (seleccionados.isEmpty()) {
            // HU-4, CA2
            JOptionPane.showMessageDialog(vista,
                    "Debe seleccionar al menos un revisor",
                    "Sin revisores", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> logins = new ArrayList<>();
        for (Usuario usuario : seleccionados) {
            logins.add(usuario.getLogin());
        }

        try {
            asignacionService.asignarRevisores(pregunta.getId(), logins,
                    SesionUsuario.getInstancia().getLogin());
            refrescar();
            // HU-4, CA1
            JOptionPane.showMessageDialog(vista,
                    "Revisor(es) asignado(s) correctamente\n\n"
                            + "Se envio la notificacion por correo a " + logins.size()
                            + " revisor(es). El detalle aparece en la consola de la aplicacion.",
                    "Operacion exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(),
                    "No fue posible asignar", JOptionPane.ERROR_MESSAGE);
        }
    }
}
