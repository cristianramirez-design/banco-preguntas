package co.edu.unicauca.bancopreguntas.presentacion;

import co.edu.unicauca.bancopreguntas.domain.model.Usuario;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IUsuarioRepository;
import co.edu.unicauca.bancopreguntas.domain.servicio.IAsignacionService;
import co.edu.unicauca.bancopreguntas.domain.servicio.IPreguntaService;
import co.edu.unicauca.bancopreguntas.presentacion.asignar.AsignarRevisorController;
import co.edu.unicauca.bancopreguntas.presentacion.asignar.AsignarRevisorView;
import co.edu.unicauca.bancopreguntas.presentacion.comun.EstiloUI;
import co.edu.unicauca.bancopreguntas.presentacion.comun.SesionUsuario;
import co.edu.unicauca.bancopreguntas.presentacion.crear.CrearPreguntaController;
import co.edu.unicauca.bancopreguntas.presentacion.crear.CrearPreguntaView;
import co.edu.unicauca.bancopreguntas.presentacion.listar.ListarPreguntasController;
import co.edu.unicauca.bancopreguntas.presentacion.listar.ListarPreguntasView;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private static final int PESTANIA_CREAR = 0;
    private static final int PESTANIA_LISTAR = 1;
    private static final int PESTANIA_ASIGNAR = 2;

    private final JTabbedPane pestanias = new JTabbedPane();
    private final JComboBox<Usuario> cmbUsuario = new JComboBox<>();
    private final JLabel lblRoles = new JLabel();

    private final CrearPreguntaController crearController;
    private final ListarPreguntasController listarController;
    private final AsignarRevisorController asignarController;

    public VentanaPrincipal(IPreguntaService preguntaService,
                            IAsignacionService asignacionService,
                            IUsuarioRepository usuarioRepository) {

        setTitle("Banco de Preguntas Saber Pro - Universidad del Cauca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 740);
        setLocationRelativeTo(null);

        CrearPreguntaView crearView = new CrearPreguntaView();
        crearController = new CrearPreguntaController(crearView, preguntaService);

        ListarPreguntasView listarView = new ListarPreguntasView();
        listarController = new ListarPreguntasController(listarView, preguntaService);

        AsignarRevisorView asignarView = new AsignarRevisorView();
        asignarController = new AsignarRevisorController(asignarView, asignacionService, usuarioRepository);

        // HU-1, CA3: al guardar o cancelar se regresa al listado del autor.
        crearController.setAlSalirDelFormulario(() -> pestanias.setSelectedIndex(PESTANIA_LISTAR));
        // HU-3, CA4: EDITAR abre el formulario de la HU-1 con los datos cargados.
        listarController.setAlEditar(pregunta -> {
            crearController.editar(pregunta);
            pestanias.setSelectedIndex(PESTANIA_CREAR);
        });

        pestanias.addTab("Crear pregunta (HU-1)", crearView);
        pestanias.addTab("Mis preguntas (HU-2 y HU-3)", listarView);
        pestanias.addTab("Preguntas pendientes (HU-4)", asignarView);
        pestanias.addChangeListener(e -> refrescarPestaniaActiva());

        add(construirBarraSuperior(usuarioRepository), BorderLayout.NORTH);
        add(pestanias, BorderLayout.CENTER);

        refrescarPestaniaActiva();
    }

    private JPanel construirBarraSuperior(IUsuarioRepository usuarioRepository) {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        barra.setBackground(EstiloUI.PRIMARIO);
        barra.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        JLabel titulo = new JLabel("Banco de Preguntas Saber Pro");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));

        JLabel etiquetaUsuario = new JLabel("Usuario:");
        etiquetaUsuario.setForeground(Color.WHITE);

        List<Usuario> usuarios = usuarioRepository.listarTodos();
        for (Usuario usuario : usuarios) {
            cmbUsuario.addItem(usuario);
        }
        cmbUsuario.addActionListener(e -> cambiarUsuario());
        lblRoles.setForeground(Color.WHITE);

        barra.add(titulo);
        barra.add(new JLabel("     "));
        barra.add(etiquetaUsuario);
        barra.add(cmbUsuario);
        barra.add(lblRoles);

        if (!usuarios.isEmpty()) {
            cmbUsuario.setSelectedIndex(0);
            cambiarUsuario();
        }
        return barra;
    }

    private void cambiarUsuario() {
        Usuario usuario = (Usuario) cmbUsuario.getSelectedItem();
        SesionUsuario.getInstancia().setUsuarioActual(usuario);
        lblRoles.setText(usuario == null ? "" : "  Roles: " + usuario.getRoles());
        refrescarPestaniaActiva();
    }

    private void refrescarPestaniaActiva() {
        int indice = pestanias.getSelectedIndex();
        if (indice == PESTANIA_LISTAR) {
            listarController.refrescar();
        } else if (indice == PESTANIA_ASIGNAR) {
            asignarController.refrescar();
        }
    }
}
