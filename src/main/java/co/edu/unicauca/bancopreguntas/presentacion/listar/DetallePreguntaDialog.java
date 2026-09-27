package co.edu.unicauca.bancopreguntas.presentacion.listar;

import co.edu.unicauca.bancopreguntas.domain.model.Opcion;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.presentacion.comun.EstiloUI;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;

/**
 * Prototipo "Detalle de pregunta" de la HU-2: campos en solo lectura,
 * acciones ENVIAR A REVISION / EDITAR y leyenda de estados.
 */
public class DetallePreguntaDialog extends JDialog {

    private final JButton btnEnviarRevision = new JButton("ENVIAR A REVISION");
    private final JButton btnEditar = new JButton("EDITAR");
    private final JButton btnCerrar = new JButton("Cerrar");

    private boolean solicitaEnviarARevision;
    private boolean solicitaEditar;

    public DetallePreguntaDialog(Frame propietario, Pregunta pregunta) {
        super(propietario, "Detalle de pregunta #" + pregunta.getCodigo(), true);
        setSize(720, 620);
        setLocationRelativeTo(propietario);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        contenido.setBackground(EstiloUI.FONDO);

        JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        encabezado.setBackground(EstiloUI.FONDO);
        JLabel titulo = new JLabel("Detalle de pregunta #" + pregunta.getCodigo());
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        encabezado.add(titulo);
        encabezado.add(EstiloUI.etiquetaEstado(pregunta.getEstado()));
        contenido.add(encabezado);

        contenido.add(soloLectura("Contexto (solo lectura)", pregunta.getContexto(), 4));
        contenido.add(soloLectura("Pregunta directa (solo lectura)", pregunta.getEnunciado(), 2));
        contenido.add(soloLectura("Opciones de respuesta", opcionesComoTexto(pregunta), 5));
        contenido.add(soloLectura("Justificacion de la respuesta", pregunta.getJustificacion(), 3));
        contenido.add(soloLectura("Bibliografia", pregunta.getBibliografia(), 2));

        JPanel metadatos = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        metadatos.setBorder(BorderFactory.createTitledBorder("Clasificacion"));
        metadatos.setBackground(EstiloUI.FONDO);
        metadatos.add(new JLabel("Competencia: " + pregunta.getCompetencia()));
        metadatos.add(new JLabel("Tema: " + pregunta.getTema()));
        metadatos.add(new JLabel("Subtema: " + pregunta.getSubtema()));
        metadatos.add(new JLabel("Nivel: "
                + (pregunta.getNivelDificultad() == null ? "" : pregunta.getNivelDificultad().getEtiqueta())));
        contenido.add(metadatos);

        if (!pregunta.getRevisores().isEmpty()) {
            contenido.add(soloLectura("Revisores asignados",
                    String.join(", ", pregunta.getRevisores()), 2));
        }

        contenido.add(EstiloUI.leyendaEstados(EstadosPregunta.valores(), true));
        contenido.add(Box.createVerticalStrut(6));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        acciones.setBackground(EstiloUI.FONDO);
        btnEnviarRevision.setEnabled(pregunta.getEstado().esEditable());
        btnEditar.setEnabled(pregunta.getEstado().esEditable());
        acciones.add(btnCerrar);
        acciones.add(btnEditar);
        acciones.add(btnEnviarRevision);

        btnEnviarRevision.addActionListener(e -> {
            solicitaEnviarARevision = true;
            dispose();
        });
        btnEditar.addActionListener(e -> {
            solicitaEditar = true;
            dispose();
        });
        btnCerrar.addActionListener(e -> dispose());

        add(new JScrollPane(contenido), BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);
    }

    private String opcionesComoTexto(Pregunta pregunta) {
        StringBuilder texto = new StringBuilder();
        int indice = 0;
        for (Opcion opcion : pregunta.getOpciones()) {
            texto.append((char) ('A' + indice)).append(") ").append(opcion.getTexto());
            if (opcion.esCorrecta()) {
                texto.append("   <-- RESPUESTA CORRECTA");
            }
            texto.append('\n');
            indice++;
        }
        return texto.toString();
    }

    private JPanel soloLectura(String titulo, String valor, int filas) {
        JTextArea area = new JTextArea(valor, filas, 40);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.setBackground(EstiloUI.FONDO);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(640, filas * 22));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    public boolean solicitaEnviarARevision() {
        return solicitaEnviarARevision;
    }

    public boolean solicitaEditar() {
        return solicitaEditar;
    }
}
