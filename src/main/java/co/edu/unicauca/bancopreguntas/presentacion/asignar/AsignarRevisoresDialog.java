package co.edu.unicauca.bancopreguntas.presentacion.asignar;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.Usuario;
import co.edu.unicauca.bancopreguntas.presentacion.comun.EstiloUI;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Modal de asignacion de la HU-4, con casillas de verificacion por revisor. */
public class AsignarRevisoresDialog extends JDialog {

    private final Map<JCheckBox, Usuario> casillas = new LinkedHashMap<>();
    private final JButton btnAsignar = new JButton("ASIGNAR REVISOR(ES)");
    private final JButton btnCancelar = new JButton("CANCELAR");

    private boolean confirmado;

    public AsignarRevisoresDialog(Frame propietario, Pregunta pregunta, List<Usuario> revisores) {
        super(propietario, "Asignar revisor(es)", true);
        setSize(440, 360);
        setLocationRelativeTo(propietario);

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBorder(BorderFactory.createEmptyBorder(12, 14, 8, 14));
        encabezado.setBackground(EstiloUI.FONDO);
        JLabel titulo = new JLabel("Asignar revisor(es)");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        JLabel subtitulo = new JLabel("Pregunta #" + pregunta.getCodigo() + " - " + pregunta.getTema());
        subtitulo.setForeground(java.awt.Color.DARK_GRAY);
        encabezado.add(titulo);
        encabezado.add(subtitulo);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        lista.setBackground(EstiloUI.FONDO);
        if (revisores.isEmpty()) {
            lista.add(new JLabel("No hay revisores disponibles para esta pregunta."));
        }
        for (Usuario revisor : revisores) {
            JCheckBox casilla = new JCheckBox(revisor.getNombre() + "  <" + revisor.getEmail() + ">");
            casilla.setBackground(EstiloUI.FONDO);
            casillas.put(casilla, revisor);
            lista.add(casilla);
        }

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        acciones.setBackground(EstiloUI.FONDO);
        acciones.add(btnCancelar);
        acciones.add(btnAsignar);

        btnAsignar.addActionListener(e -> {
            confirmado = true;
            dispose();
        });
        btnCancelar.addActionListener(e -> dispose());

        add(encabezado, BorderLayout.NORTH);
        add(new JScrollPane(lista), BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);
    }

    public boolean fueConfirmado() {
        return confirmado;
    }

    public List<Usuario> getRevisoresSeleccionados() {
        List<Usuario> seleccionados = new ArrayList<>();
        for (Map.Entry<JCheckBox, Usuario> entrada : casillas.entrySet()) {
            if (entrada.getKey().isSelected()) {
                seleccionados.add(entrada.getValue());
            }
        }
        return seleccionados;
    }
}
