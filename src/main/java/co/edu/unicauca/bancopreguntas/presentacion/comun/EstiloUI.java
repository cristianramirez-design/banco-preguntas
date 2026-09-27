package co.edu.unicauca.bancopreguntas.presentacion.comun;

import co.edu.unicauca.bancopreguntas.domain.model.estado.IEstadoPregunta;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

/** Utilidades de estilo compartidas por las vistas. */
public final class EstiloUI {

    public static final Color FONDO = new Color(0xF2F5F7);
    public static final Color PRIMARIO = new Color(0x18243A);
    public static final Color ROJO_ERROR = new Color(0xD32F2F);

    private EstiloUI() {
    }

    public static JLabel titulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 16f));
        etiqueta.setForeground(PRIMARIO);
        etiqueta.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return etiqueta;
    }

    public static Border bordeError() {
        return BorderFactory.createLineBorder(ROJO_ERROR, 2);
    }

    /** Etiqueta de estado con el color que define el propio estado (HU-2, CA3). */
    public static JLabel etiquetaEstado(IEstadoPregunta estado) {
        JLabel etiqueta = new JLabel(estado.getNombre());
        etiqueta.setOpaque(true);
        etiqueta.setBackground(Color.decode(estado.getColorHex()));
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 11f));
        etiqueta.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return etiqueta;
    }

    /** Leyenda con los cinco estados del ciclo de vida. */
    public static JPanel leyendaEstados(List<IEstadoPregunta> estados, boolean conDescripcion) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createTitledBorder("Estados del ciclo de vida de una pregunta"));
        for (IEstadoPregunta estado : estados) {
            panel.add(etiquetaEstado(estado));
            if (conDescripcion) {
                JLabel descripcion = new JLabel(estado.getDescripcion());
                descripcion.setFont(descripcion.getFont().deriveFont(Font.PLAIN, 11f));
                descripcion.setForeground(Color.DARK_GRAY);
                panel.add(descripcion);
            }
        }
        return panel;
    }

    /** Combo editable con catalogo sugerido y opcion vacia. */
    public static JComboBox<String> comboCatalogo(List<String> valores, boolean conVacio) {
        JComboBox<String> combo = new JComboBox<>();
        if (conVacio) {
            combo.addItem("");
        }
        for (String valor : valores) {
            combo.addItem(valor);
        }
        combo.setEditable(true);
        combo.setPreferredSize(new Dimension(200, 26));
        return combo;
    }

    public static String textoDe(JComboBox<String> combo) {
        Object valor = combo.isEditable() ? combo.getEditor().getItem() : combo.getSelectedItem();
        return valor == null ? "" : valor.toString().trim();
    }
}
