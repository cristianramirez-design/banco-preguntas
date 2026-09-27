package co.edu.unicauca.bancopreguntas.presentacion.crear;

import co.edu.unicauca.bancopreguntas.domain.excepcion.ValidacionException;
import co.edu.unicauca.bancopreguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.servicio.IPreguntaService;
import co.edu.unicauca.bancopreguntas.presentacion.comun.EstiloUI;
import co.edu.unicauca.bancopreguntas.presentacion.comun.SesionUsuario;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del micro-patron MVC para la HU-1.
 * Traduce los eventos de la vista en llamadas al servicio de dominio.
 */
public class CrearPreguntaController {

    private final CrearPreguntaView vista;
    private final IPreguntaService preguntaService;

    /** Callback que la ventana principal usa para volver al listado (HU-1, CA3). */
    private Runnable alSalirDelFormulario = () -> { };

    public CrearPreguntaController(CrearPreguntaView vista, IPreguntaService preguntaService) {
        this.vista = vista;
        this.preguntaService = preguntaService;
        registrarEventos();
    }

    public void setAlSalirDelFormulario(Runnable accion) {
        this.alSalirDelFormulario = accion;
    }

    public void editar(Pregunta pregunta) {
        vista.cargar(pregunta);
    }

    private void registrarEventos() {
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnCancelar().addActionListener(e -> cancelar());
    }

    private void guardar() {
        vista.limpiarResaltado();
        try {
            if (vista.estaEditando()) {
                preguntaService.actualizar(armarPregunta(), SesionUsuario.getInstancia().getLogin());
                JOptionPane.showMessageDialog(vista,
                        "Cambios guardados correctamente",
                        "Operacion exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                preguntaService.crear(armarPregunta());
                JOptionPane.showMessageDialog(vista,
                        "Pregunta creada exitosamente",
                        "Operacion exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
            vista.limpiar();
            alSalirDelFormulario.run();
        } catch (ValidacionException ex) {
            vista.resaltarCampos(ex.getCamposInvalidos());
            mostrarErrores(ex.getErrores());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** HU-1, CA3: descarta los cambios y regresa al listado del autor. */
    private void cancelar() {
        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "Se descartaran los cambios no guardados. Desea continuar?",
                "Cancelar", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            vista.limpiar();
            alSalirDelFormulario.run();
        }
    }

    private Pregunta armarPregunta() {
        List<String> textos = new ArrayList<>();
        int indiceCorrecta = -1;
        for (int i = 0; i < vista.getTxtOpciones().length; i++) {
            textos.add(vista.getTxtOpciones()[i].getText());
            if (vista.getRadOpciones()[i].isSelected()) {
                indiceCorrecta = i;
            }
        }

        Pregunta.Builder builder = vista.estaEditando()
                ? vista.getPreguntaEnEdicion().aBuilder()
                : new Pregunta.Builder().autorLogin(SesionUsuario.getInstancia().getLogin());

        return builder
                .contexto(vista.getTxtContexto().getText())
                .enunciado(vista.getTxtEnunciado().getText())
                .opcionesConCorrecta(textos, indiceCorrecta)
                .justificacion(vista.getTxtJustificacion().getText())
                .bibliografia(vista.getTxtBibliografia().getText())
                .competencia(EstiloUI.textoDe(vista.getCmbCompetencia()))
                .tema(EstiloUI.textoDe(vista.getCmbTema()))
                .subtema(EstiloUI.textoDe(vista.getCmbSubtema()))
                .nivelDificultad((NivelDificultad) vista.getCmbDificultad().getSelectedItem())
                .construir();
    }

    private void mostrarErrores(List<String> errores) {
        StringBuilder mensaje = new StringBuilder(ValidacionException.MENSAJE_USUARIO);
        mensaje.append("\n\nSe resaltaron en rojo los campos invalidos o faltantes:\n\n");
        for (String error : errores) {
            mensaje.append("  - ").append(error).append('\n');
        }
        JOptionPane.showMessageDialog(vista, mensaje.toString(),
                "Validacion estructural", JOptionPane.WARNING_MESSAGE);
    }
}
