package co.edu.unicauca.bancopreguntas.presentacion.asignar;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Columnas exigidas por la HU-4, CA3: ID, Autor, Tema, Competencia,
 * Fecha de envio y Estado.
 */
public class PendientesTableModel extends AbstractTableModel {

    public static final int COL_ESTADO = 5;

    private static final String[] COLUMNAS = {
            "ID", "Autor", "Tema", "Competencia", "Fecha envio", "Estado"
    };

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private List<Pregunta> preguntas = new ArrayList<>();
    private Function<String, String> resolverNombreAutor = login -> login;

    public void setResolverNombreAutor(Function<String, String> resolver) {
        this.resolverNombreAutor = resolver;
    }

    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = new ArrayList<>(preguntas);
        fireTableDataChanged();
    }

    public Pregunta getPreguntaEn(int fila) {
        return preguntas.get(fila);
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
                return resolverNombreAutor.apply(pregunta.getAutorLogin());
            case 2:
                return pregunta.getTema();
            case 3:
                return pregunta.getCompetencia();
            case 4:
                return pregunta.getFechaEnvioRevision() == null
                        ? "" : pregunta.getFechaEnvioRevision().format(FORMATO);
            case COL_ESTADO:
                return pregunta.getEstado().getNombre();
            default:
                return "";
        }
    }
}
