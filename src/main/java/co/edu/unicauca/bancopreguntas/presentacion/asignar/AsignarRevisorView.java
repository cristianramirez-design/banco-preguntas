package co.edu.unicauca.bancopreguntas.presentacion.asignar;

import co.edu.unicauca.bancopreguntas.presentacion.comun.EstiloUI;
import co.edu.unicauca.bancopreguntas.presentacion.listar.EstadoCellRenderer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/** Vista MVC de la HU-4: bandeja de preguntas pendientes de revision. */
public class AsignarRevisorView extends JPanel {

    private final PendientesTableModel tableModel = new PendientesTableModel();
    private final JTable tabla = new JTable(tableModel);
    private final JButton btnAsignar = new JButton("ASIGNAR REVISOR(ES)");
    private final JButton btnRefrescar = new JButton("Refrescar");
    private final JLabel lblEstado = new JLabel(" ");

    public AsignarRevisorView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setBackground(EstiloUI.FONDO);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(26);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(PendientesTableModel.COL_ESTADO)
                .setCellRenderer(new EstadoCellRenderer());

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Preguntas pendientes de revision"));
        panelTabla.setBackground(EstiloUI.FONDO);
        panelTabla.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        acciones.setBackground(EstiloUI.FONDO);
        acciones.add(lblEstado);
        acciones.add(btnRefrescar);
        acciones.add(btnAsignar);

        add(EstiloUI.titulo("HU-4 - Asignar revisores"), BorderLayout.NORTH);
        add(panelTabla, BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);
    }

    public PendientesTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTabla() {
        return tabla;
    }

    public JButton getBtnAsignar() {
        return btnAsignar;
    }

    public JButton getBtnRefrescar() {
        return btnRefrescar;
    }

    public JLabel getLblEstado() {
        return lblEstado;
    }
}
