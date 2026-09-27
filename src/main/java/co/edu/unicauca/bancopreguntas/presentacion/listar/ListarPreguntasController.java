package co.edu.unicauca.bancopreguntas.presentacion.listar;

import co.edu.unicauca.bancopreguntas.domain.busqueda.CriterioBusqueda;
import co.edu.unicauca.bancopreguntas.domain.busqueda.PaginaResultado;
import co.edu.unicauca.bancopreguntas.domain.excepcion.PreguntaIncompletaException;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.estado.IEstadoPregunta;
import co.edu.unicauca.bancopreguntas.domain.servicio.IPreguntaService;
import co.edu.unicauca.bancopreguntas.presentacion.comun.EstiloUI;
import co.edu.unicauca.bancopreguntas.presentacion.comun.SesionUsuario;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/** Controlador MVC de la HU-3 y disparador de la HU-2. */
public class ListarPreguntasController {

    private final ListarPreguntasView vista;
    private final IPreguntaService preguntaService;

    private int paginaActual = 1;
    private Consumer<Pregunta> alEditar = pregunta -> { };

    public ListarPreguntasController(ListarPreguntasView vista, IPreguntaService preguntaService) {
        this.vista = vista;
        this.preguntaService = preguntaService;
        registrarEventos();
    }

    /** La ventana principal inyecta la accion que abre el formulario de edicion. */
    public void setAlEditar(Consumer<Pregunta> accion) {
        this.alEditar = accion;
    }

    private void registrarEventos() {
        vista.getBtnFiltrar().addActionListener(e -> {
            paginaActual = 1;
            refrescar();
        });
        vista.getBtnLimpiarFiltros().addActionListener(e -> limpiarFiltros());
        vista.getBtnAnterior().addActionListener(e -> {
            if (paginaActual > 1) {
                paginaActual--;
                refrescar();
            }
        });
        vista.getBtnSiguiente().addActionListener(e -> {
            paginaActual++;
            refrescar();
        });
        vista.getCmbTamanioPagina().addActionListener(e -> {
            paginaActual = 1;
            refrescar();
        });
        vista.getBtnVer().addActionListener(e -> verDetalle());
        vista.getBtnEditar().addActionListener(e -> editarSeleccionada());
        vista.getBtnEnviarRevision().addActionListener(e -> enviarARevision());

        // Columna Acciones: la mitad izquierda es VER, la derecha EDITAR.
        vista.getTabla().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                int fila = vista.getTabla().rowAtPoint(evento.getPoint());
                int columna = vista.getTabla().columnAtPoint(evento.getPoint());
                if (fila < 0) {
                    return;
                }
                vista.getTabla().setRowSelectionInterval(fila, fila);
                if (columna != PreguntaTableModel.COL_ACCIONES) {
                    if (evento.getClickCount() == 2) {
                        verDetalle();
                    }
                    return;
                }
                int centro = vista.getTabla().getCellRect(fila, columna, true).x
                        + vista.getTabla().getCellRect(fila, columna, true).width / 2;
                if (evento.getX() < centro) {
                    verDetalle();
                } else {
                    editarSeleccionada();
                }
            }
        });
    }

    private void limpiarFiltros() {
        vista.getTxtBusqueda().setText("");
        vista.getCmbEstado().setSelectedIndex(0);
        vista.getCmbTema().setSelectedItem("");
        vista.getCmbSubtema().setSelectedItem("");
        vista.getCmbCompetencia().setSelectedItem("");
        paginaActual = 1;
        refrescar();
    }

    public void refrescar() {
        CriterioBusqueda criterio = new CriterioBusqueda()
                .autor(SesionUsuario.getInstancia().getLogin())
                .texto(vista.getTxtBusqueda().getText())
                .tema(EstiloUI.textoDe(vista.getCmbTema()))
                .subtema(EstiloUI.textoDe(vista.getCmbSubtema()))
                .competencia(EstiloUI.textoDe(vista.getCmbCompetencia()));

        Object estadoSeleccionado = vista.getCmbEstado().getSelectedItem();
        if (estadoSeleccionado instanceof IEstadoPregunta) {
            criterio.estado((IEstadoPregunta) estadoSeleccionado);
        }

        int tamanio = (Integer) vista.getCmbTamanioPagina().getSelectedItem();
        PaginaResultado<Pregunta> pagina = preguntaService.listar(criterio, paginaActual, tamanio);

        if (pagina.estaVacia() && paginaActual > 1) {
            paginaActual = pagina.getTotalPaginas();
            pagina = preguntaService.listar(criterio, paginaActual, tamanio);
        }

        vista.getTableModel().setPreguntas(pagina.getContenido());
        vista.mostrarMensajeSinResultados(pagina.getTotalElementos() == 0);
        vista.getLblPaginacion().setText(String.format("  Pagina %d de %d  -  %d pregunta(s)  ",
                pagina.getPaginaActual(), pagina.getTotalPaginas(), pagina.getTotalElementos()));
        vista.getBtnAnterior().setEnabled(pagina.hayPaginaAnterior());
        vista.getBtnSiguiente().setEnabled(pagina.hayPaginaSiguiente());
    }

    private Pregunta seleccionada() {
        int fila = vista.getTabla().getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione primero una pregunta de la tabla.",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return vista.getTableModel().getPreguntaEn(fila);
    }

    private void verDetalle() {
        Pregunta pregunta = seleccionada();
        if (pregunta == null) {
            return;
        }
        Frame propietario = (Frame) SwingUtilities.getWindowAncestor(vista);
        DetallePreguntaDialog dialogo = new DetallePreguntaDialog(propietario, pregunta);
        dialogo.setVisible(true);

        if (dialogo.solicitaEnviarARevision()) {
            ejecutarEnvio(pregunta);
        } else if (dialogo.solicitaEditar()) {
            alEditar.accept(pregunta);
        }
    }

    /** HU-3, CA4: solo se editan preguntas en estado editable. */
    private void editarSeleccionada() {
        Pregunta pregunta = seleccionada();
        if (pregunta == null) {
            return;
        }
        if (!pregunta.esEditable()) {
            JOptionPane.showMessageDialog(vista,
                    "Solo se pueden editar preguntas en estado Borrador o Rechazada.\n"
                            + "Estado actual: " + pregunta.getEstado().getNombre() + ".",
                    "Edicion no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        alEditar.accept(pregunta);
    }

    private void enviarARevision() {
        Pregunta pregunta = seleccionada();
        if (pregunta == null) {
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "Enviar a revision la pregunta #" + pregunta.getCodigo() + "?\n\n"
                        + pregunta.getEnunciado(),
                "Confirmar cambio de estado", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            ejecutarEnvio(pregunta);
        }
    }

    private void ejecutarEnvio(Pregunta pregunta) {
        try {
            preguntaService.enviarARevision(pregunta.getId(), SesionUsuario.getInstancia().getLogin());
            refrescar();
            JOptionPane.showMessageDialog(vista,
                    "La pregunta paso al estado Pendiente de revision.",
                    "Operacion exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (PreguntaIncompletaException ex) {
            StringBuilder mensaje = new StringBuilder(PreguntaIncompletaException.MENSAJE_USUARIO);
            mensaje.append("\n\n");
            for (String error : ex.getErrores()) {
                mensaje.append("  - ").append(error).append('\n');
            }
            JOptionPane.showMessageDialog(vista, mensaje.toString(),
                    "Pregunta incompleta", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(),
                    "No fue posible cambiar el estado", JOptionPane.ERROR_MESSAGE);
        }
    }
}
