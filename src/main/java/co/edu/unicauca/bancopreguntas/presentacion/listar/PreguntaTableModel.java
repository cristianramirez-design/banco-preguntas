package co.edu.unicauca.bancopreguntas.presentacion.listar;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de la vista (parte "Model" del micro-patron MVC) con las columnas
 * exigidas por la HU-3, CA1: ID, Pregunta directa, Tema, Competencia,
 * Nivel de dificultad, Estado y Acciones.
 */
public class PreguntaTableModel extends AbstractTableModel {

    public static final int COL_ESTADO = 5;
    public static final int COL_ACCIONES = 6;

    private static final String[] COLUMNAS = {
            "ID", "Pregunta directa", "Tema", "Competencia", "Nivel", "Estado", "Acciones"
    };

    private List<Pregunta> preguntas = new ArrayList<>();

    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = new ArrayList<>(preguntas);
        fireTableDataChanged();
    }

    public Pregunta getPreguntaEn(int fila) {
        return preguntas.get(fila);
    }

    public boolean estaVacio() {
        return preguntas.isEmpty();
    }

    @Override
    public int getRowCount() {
        return preguntas.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNAS[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Pregunta pregunta = preguntas.get(fila);
        switch (columna) {
            case 0:
                return "#" + pregunta.getCodigo();
            case 1:
                return pregunta.getEnunciado();
            case 2:
                return pregunta.getTema();
            case 3:
                return pregunta.getCompetencia();
            case 4:
                return pregunta.getNivelDificultad() == null
                        ? "" : pregunta.getNivelDificultad().getEtiqueta();
            case COL_ESTADO:
                return pregunta.getEstado().getNombre();
            case COL_ACCIONES:
                return "VER  |  EDITAR";
            default:
                return "";
        }
    }
}
