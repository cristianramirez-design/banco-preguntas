package co.edu.unicauca.bancopreguntas.presentacion.listar;

import co.edu.unicauca.bancopreguntas.domain.model.Catalogos;
import co.edu.unicauca.bancopreguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.domain.model.estado.IEstadoPregunta;
import co.edu.unicauca.bancopreguntas.presentacion.comun.EstiloUI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

/** Vista MVC de la HU-3: listado del autor con filtros y paginacion. */
public class ListarPreguntasView extends JPanel {

    private final JTextField txtBusqueda = new JTextField(16);
    private final JComboBox<Object> cmbEstado = new JComboBox<>();
    private final JComboBox<String> cmbTema = EstiloUI.comboCatalogo(Catalogos.temas(), true);
    private final JComboBox<String> cmbSubtema = EstiloUI.comboCatalogo(Catalogos.subtemas(), true);
    private final JComboBox<String> cmbCompetencia = EstiloUI.comboCatalogo(Catalogos.competencias(), true);
    private final JComboBox<Integer> cmbTamanioPagina = new JComboBox<>(new Integer[]{5, 10, 20});
    private final JButton btnFiltrar = new JButton("FILTRAR");
    private final JButton btnLimpiarFiltros = new JButton("Limpiar filtros");
    private final JButton btnAnterior = new JButton("<");
    private final JButton btnSiguiente = new JButton(">");
    private final JButton btnVer = new JButton("VER DETALLE");
    private final JButton btnEditar = new JButton("EDITAR");
    private final JButton btnEnviarRevision = new JButton("ENVIAR A REVISION");
    private final JLabel lblPaginacion = new JLabel(" ");
    private final JLabel lblSinResultados = new JLabel(" ");
    private final PreguntaTableModel tableModel = new PreguntaTableModel();
    private final JTable tabla = new JTable(tableModel);

    public ListarPreguntasView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setBackground(EstiloUI.FONDO);

        cmbEstado.addItem("Todos los estados");
        for (IEstadoPregunta estado : EstadosPregunta.valores()) {
            cmbEstado.addItem(estado);
        }
        cmbTamanioPagina.setSelectedItem(10);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(26);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(280);
        tabla.getColumnModel().getColumn(PreguntaTableModel.COL_ESTADO)
                .setCellRenderer(new EstadoCellRenderer());
        tabla.getColumnModel().getColumn(PreguntaTableModel.COL_ACCIONES)
                .setCellRenderer(new AccionesCellRenderer());
        tabla.getColumnModel().getColumn(PreguntaTableModel.COL_ACCIONES).setPreferredWidth(110);

        lblSinResultados.setForeground(EstiloUI.ROJO_ERROR);
        lblSinResultados.setFont(lblSinResultados.getFont().deriveFont(Font.ITALIC, 12f));

        add(EstiloUI.titulo("HU-3 - Mis preguntas creadas"), BorderLayout.NORTH);
        add(construirPanelCentral(), BorderLayout.CENTER);
        add(construirPanelInferior(), BorderLayout.SOUTH);
    }

    private JPanel construirPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(EstiloUI.FONDO);
        panel.add(construirPanelFiltros(), BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(lblSinResultados, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Filtros de busqueda"));
        panel.setBackground(EstiloUI.FONDO);
        panel.add(new JLabel("Buscar por ID o contenido:"));
        panel.add(txtBusqueda);
        panel.add(new JLabel("Estado:"));
        panel.add(cmbEstado);
        panel.add(new JLabel("Tema:"));
        panel.add(cmbTema);
        panel.add(new JLabel("Subtema:"));
        panel.add(cmbSubtema);
        panel.add(new JLabel("Competencia:"));
        panel.add(cmbCompetencia);
        panel.add(new JLabel("Por pagina:"));
        panel.add(cmbTamanioPagina);
        panel.add(btnFiltrar);
        panel.add(btnLimpiarFiltros);
        return panel;
    }

    private JPanel construirPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(EstiloUI.FONDO);

        JPanel paginacion = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        paginacion.setBackground(EstiloUI.FONDO);
        paginacion.add(btnAnterior);
        paginacion.add(lblPaginacion);
        paginacion.add(btnSiguiente);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        acciones.setBackground(EstiloUI.FONDO);
        acciones.add(btnVer);
        acciones.add(btnEditar);
        acciones.add(btnEnviarRevision);

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(EstiloUI.FONDO);
        barra.add(paginacion, BorderLayout.WEST);
        barra.add(acciones, BorderLayout.EAST);

        panel.add(EstiloUI.leyendaEstados(EstadosPregunta.valores(), false), BorderLayout.NORTH);
        panel.add(barra, BorderLayout.SOUTH);
        return panel;
    }

    public void mostrarMensajeSinResultados(boolean mostrar) {
        lblSinResultados.setText(mostrar
                ? "No se encontraron preguntas con los criterios seleccionados" : " ");
    }

    public JTextField getTxtBusqueda() {
        return txtBusqueda;
    }

    public JComboBox<Object> getCmbEstado() {
        return cmbEstado;
    }

    public JComboBox<String> getCmbTema() {
        return cmbTema;
    }

    public JComboBox<String> getCmbSubtema() {
        return cmbSubtema;
    }

    public JComboBox<String> getCmbCompetencia() {
        return cmbCompetencia;
    }

    public JComboBox<Integer> getCmbTamanioPagina() {
        return cmbTamanioPagina;
    }

    public JButton getBtnFiltrar() {
        return btnFiltrar;
    }

    public JButton getBtnLimpiarFiltros() {
        return btnLimpiarFiltros;
    }

    public JButton getBtnAnterior() {
        return btnAnterior;
    }

    public JButton getBtnSiguiente() {
        return btnSiguiente;
    }

    public JButton getBtnVer() {
        return btnVer;
    }

    public JButton getBtnEditar() {
        return btnEditar;
    }

    public JButton getBtnEnviarRevision() {
        return btnEnviarRevision;
    }

    public JLabel getLblPaginacion() {
        return lblPaginacion;
    }

    public PreguntaTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTabla() {
        return tabla;
    }
}
