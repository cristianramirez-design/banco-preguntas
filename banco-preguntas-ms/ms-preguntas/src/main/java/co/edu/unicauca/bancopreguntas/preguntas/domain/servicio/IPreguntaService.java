package co.edu.unicauca.bancopreguntas.preguntas.domain.servicio;

import co.edu.unicauca.bancopreguntas.preguntas.domain.busqueda.CriterioBusqueda;
import co.edu.unicauca.bancopreguntas.preguntas.domain.busqueda.PaginaResultado;
import co.edu.unicauca.bancopreguntas.preguntas.domain.model.Pregunta;

import java.util.List;
import java.util.Optional;

public interface IPreguntaService {

    /** HU-1, CA1: crea la pregunta aplicando la validacion estructural. */
    Pregunta crear(Pregunta pregunta);

    /** HU-3, CA4: guarda los cambios de una pregunta editable. */
    Pregunta actualizar(Pregunta pregunta, String loginSolicitante);

    /** HU-2, CA1: cambia el estado de Borrador a Pendiente de revision y publica el evento. */
    Pregunta enviarARevision(String idPregunta, String loginSolicitante);

    /** HU-3, CA1 y CA2: listado del autor con filtros y paginacion. */
    PaginaResultado<Pregunta> listar(CriterioBusqueda criterio, int pagina, int tamanioPagina);

    Optional<Pregunta> buscarPorId(String idPregunta);

    /** Reaccion al evento RevisoresAsignados (HU-4): Pendiente de revision -> En revision. */
    void marcarEnRevision(String idPregunta, List<String> revisores);

    /** Reaccion al evento PreguntaEvaluada (HU-5): En revision -> Aprobada o Rechazada. */
    void registrarResultadoRevision(String idPregunta, boolean aprobada);
}
