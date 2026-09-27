package co.edu.unicauca.bancopreguntas.presentacion.listar;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

/** Columna Acciones de la HU-3: enlaces VER y EDITAR. */
public class AccionesCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                   boolean conFoco, int fila, int columna) {
        JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(
                tabla, valor, seleccionada, conFoco, fila, columna);
        etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
        etiqueta.setForeground(new Color(0x1565C0));
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 11f));
        return etiqueta;
    }
}
