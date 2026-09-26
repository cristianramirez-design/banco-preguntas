package co.edu.unicauca.bancopreguntas.domain.servicio;

import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.Usuario;
import co.edu.unicauca.bancopreguntas.domain.notificacion.IObservadorAsignacion;

import java.util.List;

public interface IAsignacionService {

    List<Pregunta> listarPendientesDeRevision();

    List<Usuario> listarRevisoresDisponibles(String idPregunta);

    /** HU04: asigna al menos un revisor y dispara la notificacion por correo. */
    Pregunta asignarRevisores(String idPregunta, List<String> loginsRevisores, String loginAdministrador);

    void registrarObservador(IObservadorAsignacion observador);
}
