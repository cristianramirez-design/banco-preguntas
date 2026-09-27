package co.edu.unicauca.bancopreguntas.presentacion.crear;

import co.edu.unicauca.bancopreguntas.domain.model.Catalogos;
import co.edu.unicauca.bancopreguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.domain.model.Opcion;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.CampoPregunta;
import co.edu.unicauca.bancopreguntas.domain.validacion.ValidadorFactory;
import co.edu.unicauca.bancopreguntas.presentacion.comun.EstiloUI;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Vista del micro-patron MVC para la HU-1. Solo construye y expone widgets:
 * no contiene reglas de negocio ni validaciones.
 */
public class CrearPreguntaView extends JPanel {

    private static final int OPCIONES = ValidadorFactory.OPCIONES_POR_PREGUNTA;

    private final JLabel lblTitulo = EstiloUI.titulo("HU-1 - Crear pregunta de seleccion multiple");
    private final JTextArea txtContexto = new JTextArea(4, 40);
    private final JTextArea txtEnunciado = new JTextArea(2, 40);
    private final JTextField[] txtOpciones = new JTextField[OPCIONES];
    private final JRadioButton[] radOpciones = new JRadioButton[OPCIONES];
    private final ButtonGroup grupoRespuesta = new ButtonGroup();
    private final JTextArea txtJustificacion = new JTextArea(3, 40);
    private final JTextArea txtBibliografia = new JTextArea(2, 40);
    private final JComboBox<String> cmbCompetencia = EstiloUI.comboCatalogo(Catalogos.competencias(), true);
    private final JComboBox<String> cmbTema = EstiloUI.comboCatalogo(Catalogos.temas(), true);
    private final JComboBox<String> cmbSubtema = EstiloUI.comboCatalogo(Catalogos.subtemas(), true);
    private final JComboBox<NivelDificultad> cmbDificultad = new JComboBox<>(NivelDificultad.values());
    private final JButton btnGuardar = new JButton("GUARDAR PREGUNTA");
    private final JButton btnCancelar = new JButton("CANCELAR");
    private final JLabel lblAviso = new JLabel("Al GUARDAR se aplica la validacion estructural.");

    private final JPanel panelOpciones = new JPanel(new GridLayout(OPCIONES, 1, 4, 4));
    private final Map<JComponent, Border> bordesOriginales = new HashMap<>();

    private Pregunta preguntaEnEdicion;

    public CrearPreguntaView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setBackground(EstiloUI.FONDO);

        cmbDificultad.setSelectedIndex(-1);
        lblAviso.setFont(lblAviso.getFont().deriveFont(Font.ITALIC, 11f));

        add(lblTitulo, BorderLayout.NORTH);
        add(new JScrollPane(construirFormulario()), BorderLayout.CENTER);
        add(construirBotonera(), BorderLayout.SOUTH);
    }

    private JPanel construirFormulario() {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(EstiloUI.FONDO);

        contenido.add(bloqueArea("Contexto (situacion problema)", txtContexto));
        contenido.add(bloqueArea("Pregunta directa (debe terminar en '?')", txtEnunciado));

        panelOpciones.setBorder(tituloOpciones());
        panelOpciones.setBackground(EstiloUI.FONDO);
        for (int i = 0; i < OPCIONES; i++) {
            txtOpciones[i] = new JTextField();
            radOpciones[i] = new JRadioButton("Correcta");
            radOpciones[i].setBackground(EstiloUI.FONDO);
            grupoRespuesta.add(radOpciones[i]);

            JPanel fila = new JPanel(new BorderLayout(6, 0));
            fila.setBackground(EstiloUI.FONDO);
            JLabel etiqueta = new JLabel("Opcion " + (char) ('A' + i) + ":");
            etiqueta.setPreferredSize(new Dimension(70, 24));
            fila.add(etiqueta, BorderLayout.WEST);
            fila.add(txtOpciones[i], BorderLayout.CENTER);
            fila.add(radOpciones[i], BorderLayout.EAST);
            panelOpciones.add(fila);
        }
        contenido.add(panelOpciones);

        contenido.add(bloqueArea("Justificacion de la respuesta", txtJustificacion));
        contenido.add(bloqueArea("Bibliografia / Referencias", txtBibliografia));

        JPanel clasificacion = new JPanel(new GridLayout(4, 2, 8, 6));
        clasificacion.setBorder(BorderFactory.createTitledBorder("Metadatos de la pregunta"));
        clasificacion.setBackground(EstiloUI.FONDO);
        clasificacion.add(new JLabel("Competencia:"));
        clasificacion.add(cmbCompetencia);
        clasificacion.add(new JLabel("Tema:"));
        clasificacion.add(cmbTema);
        clasificacion.add(new JLabel("Subtema:"));
        clasificacion.add(cmbSubtema);
        clasificacion.add(new JLabel("Nivel de dificultad:"));
        clasificacion.add(cmbDificultad);
        contenido.add(clasificacion);

        contenido.add(Box.createVerticalStrut(8));
        return contenido;
    }

    private Border tituloOpciones() {
        return BorderFactory.createTitledBorder(OPCIONES + " opciones de respuesta ("
                + (OPCIONES - 1) + " distractores y la respuesta correcta)"
                + " - marque cual es la correcta");
    }

    private JPanel bloqueArea(String titulo, JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.setBackground(EstiloUI.FONDO);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(620, area.getRows() * 22));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirBotonera() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EstiloUI.FONDO);

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        izquierda.setBackground(EstiloUI.FONDO);
        izquierda.add(lblAviso);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        derecha.setBackground(EstiloUI.FONDO);
        derecha.add(btnCancelar);
        derecha.add(btnGuardar);

        panel.add(izquierda, BorderLayout.WEST);
        panel.add(derecha, BorderLayout.EAST);
        return panel;
    }

    // ------------------------------------------------------------------
    // Estado del formulario
    // ------------------------------------------------------------------

    public void limpiar() {
        preguntaEnEdicion = null;
        lblTitulo.setText("HU-1 - Crear pregunta de seleccion multiple");
        btnGuardar.setText("GUARDAR PREGUNTA");
        txtContexto.setText("");
        txtEnunciado.setText("");
        for (int i = 0; i < OPCIONES; i++) {
            txtOpciones[i].setText("");
            radOpciones[i].setSelected(false);
        }
        grupoRespuesta.clearSelection();
        txtJustificacion.setText("");
        txtBibliografia.setText("");
        cmbCompetencia.setSelectedItem("");
        cmbTema.setSelectedItem("");
        cmbSubtema.setSelectedItem("");
        cmbDificultad.setSelectedIndex(-1);
        limpiarResaltado();
    }

    /** HU-3, CA4: carga la pregunta seleccionada para editarla. */
    public void cargar(Pregunta pregunta) {
        limpiar();
        preguntaEnEdicion = pregunta;
        lblTitulo.setText("HU-1 - Editar pregunta " + pregunta.getCodigo());
        btnGuardar.setText("GUARDAR CAMBIOS");
        txtContexto.setText(pregunta.getContexto());
        txtEnunciado.setText(pregunta.getEnunciado());

        List<Opcion> opciones = pregunta.getOpciones();
        for (int i = 0; i < OPCIONES && i < opciones.size(); i++) {
            txtOpciones[i].setText(opciones.get(i).getTexto());
            radOpciones[i].setSelected(opciones.get(i).esCorrecta());
        }
        txtJustificacion.setText(pregunta.getJustificacion());
        txtBibliografia.setText(pregunta.getBibliografia());
        cmbCompetencia.setSelectedItem(pregunta.getCompetencia());
        cmbTema.setSelectedItem(pregunta.getTema());
        cmbSubtema.setSelectedItem(pregunta.getSubtema());
        cmbDificultad.setSelectedItem(pregunta.getNivelDificultad());
    }

    public Pregunta getPreguntaEnEdicion() {
        return preguntaEnEdicion;
    }

    public boolean estaEditando() {
        return preguntaEnEdicion != null;
    }

    /** HU-1, CA2: resalta en rojo los campos invalidos o faltantes. */
    public void resaltarCampos(Set<CampoPregunta> campos) {
        limpiarResaltado();
        for (CampoPregunta campo : campos) {
            for (JComponent componente : componentesDe(campo)) {
                bordesOriginales.putIfAbsent(componente, componente.getBorder());
                componente.setBorder(EstiloUI.bordeError());
            }
            if (campo == CampoPregunta.OPCIONES || campo == CampoPregunta.RESPUESTA_CORRECTA) {
                panelOpciones.setBorder(BorderFactory.createTitledBorder(
                        EstiloUI.bordeError(), "Revise las opciones de respuesta"));
            }
        }
    }

    public void limpiarResaltado() {
        for (Map.Entry<JComponent, Border> entrada : bordesOriginales.entrySet()) {
            entrada.getKey().setBorder(entrada.getValue());
        }
        bordesOriginales.clear();
        panelOpciones.setBorder(tituloOpciones());
    }

    private List<JComponent> componentesDe(CampoPregunta campo) {
        List<JComponent> componentes = new ArrayList<>();
        switch (campo) {
            case CONTEXTO:
                componentes.add(txtContexto);
                break;
            case ENUNCIADO:
                componentes.add(txtEnunciado);
                break;
            case OPCIONES:
            case RESPUESTA_CORRECTA:
                for (JTextField campoTexto : txtOpciones) {
                    if (campoTexto.getText().isBlank()) {
                        componentes.add(campoTexto);
                    }
                }
                break;
            case JUSTIFICACION:
                componentes.add(txtJustificacion);
                break;
            case BIBLIOGRAFIA:
                componentes.add(txtBibliografia);
                break;
            case COMPETENCIA:
                componentes.add(cmbCompetencia);
                break;
            case TEMA:
                componentes.add(cmbTema);
                break;
            case SUBTEMA:
                componentes.add(cmbSubtema);
                break;
            case NIVEL_DIFICULTAD:
                componentes.add(cmbDificultad);
                break;
            default:
                break;
        }
        return componentes;
    }

    // Accesores usados por el controlador
    public JTextArea getTxtContexto() {
        return txtContexto;
    }

    public JTextArea getTxtEnunciado() {
        return txtEnunciado;
    }

    public JTextField[] getTxtOpciones() {
        return txtOpciones;
    }

    public JRadioButton[] getRadOpciones() {
        return radOpciones;
    }

    public JTextArea getTxtJustificacion() {
        return txtJustificacion;
    }

    public JTextArea getTxtBibliografia() {
        return txtBibliografia;
    }

    public JComboBox<String> getCmbCompetencia() {
        return cmbCompetencia;
    }

    public JComboBox<String> getCmbTema() {
        return cmbTema;
    }

    public JComboBox<String> getCmbSubtema() {
        return cmbSubtema;
    }

    public JComboBox<NivelDificultad> getCmbDificultad() {
        return cmbDificultad;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }
}
