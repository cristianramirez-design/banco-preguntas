package co.edu.unicauca.bancopreguntas.domain.servicio;

import co.edu.unicauca.bancopreguntas.domain.busqueda.CriterioBusqueda;
import co.edu.unicauca.bancopreguntas.domain.busqueda.PaginaResultado;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;

import java.util.Optional;

public interface IPreguntaService {

    /** HU-1, CA1: crea la pregunta aplicando la validacion estructural. */
    Pregunta crear(Pregunta pregunta);

    /** HU-3, CA4: guarda los cambios de una pregunta editable. */
    Pregunta actualizar(Pregunta pregunta, String loginSolicitante);

    /** HU-2, CA1: cambia el estado de Borrador a Pendiente de revision. */
    Pregunta enviarARevision(String idPregunta, String loginSolicitante);

    /** HU-3, CA1 y CA2: listado del autor con filtros y paginacion. */
    PaginaResultado<Pregunta> listar(CriterioBusqueda criterio, int pagina, int tamanioPagina);

    Optional<Pregunta> buscarPorId(String idPregunta);
}
