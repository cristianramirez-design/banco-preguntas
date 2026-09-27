package co.edu.unicauca.bancopreguntas.presentacion.listar;

import co.edu.unicauca.bancopreguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.domain.model.estado.IEstadoPregunta;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

/**
 * HU-2, CA3: los estados se visualizan con colores. El color lo define el
 * propio estado (patron State); la vista solo lo pinta.
 */
public class EstadoCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                   boolean conFoco, int fila, int columna) {
        JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(
                tabla, valor, seleccionada, conFoco, fila, columna);

        etiqueta.setOpaque(true);
        etiqueta.setBackground(colorDe(String.valueOf(valor)));
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 11f));
        etiqueta.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        return etiqueta;
    }

    private Color colorDe(String nombreEstado) {
        for (IEstadoPregunta estado : EstadosPregunta.valores()) {
            if (estado.getNombre().equals(nombreEstado)) {
                return Color.decode(estado.getColorHex());
            }
        }
        return Color.GRAY;
    }
}
