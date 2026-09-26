package co.edu.unicauca.bancopreguntas.domain;

import co.edu.unicauca.bancopreguntas.acceso.memoria.GeneradorCodigoSecuencial;
import co.edu.unicauca.bancopreguntas.acceso.memoria.PreguntaRepositoryMemoria;
import co.edu.unicauca.bancopreguntas.domain.busqueda.CriterioBusqueda;
import co.edu.unicauca.bancopreguntas.domain.busqueda.PaginaResultado;
import co.edu.unicauca.bancopreguntas.domain.excepcion.PreguntaIncompletaException;
import co.edu.unicauca.bancopreguntas.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.domain.excepcion.ValidacionException;
import co.edu.unicauca.bancopreguntas.domain.model.NivelDificultad;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.domain.repositorio.IPreguntaRepository;
import co.edu.unicauca.bancopreguntas.domain.servicio.IPreguntaService;
import co.edu.unicauca.bancopreguntas.domain.servicio.PreguntaService;
import co.edu.unicauca.bancopreguntas.domain.validacion.ValidadorFactory;
import co.edu.unicauca.bancopreguntas.util.PreguntaMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PreguntaServiceTest {

    private IPreguntaRepository repositorio;
    private IPreguntaService servicio;

    @BeforeEach
    void inicializar() {
        repositorio = new PreguntaRepositoryMemoria();
        servicio = new PreguntaService(repositorio, ValidadorFactory.porDefecto(),
                new GeneradorCodigoSecuencial("A", 227));
    }

    @Test
    @DisplayName("HU-1, CA1: crear persiste la pregunta y le asigna un codigo legible")
    void crearPreguntaValida() {
        Pregunta pregunta = servicio.crear(PreguntaMother.valida().construir());

        assertTrue(repositorio.buscarPorId(pregunta.getId()).isPresent());
        assertEquals("A-0227", pregunta.getCodigo());
        assertEquals(1, repositorio.listarTodas().size());
    }

    @Test
    @DisplayName("Los codigos se asignan de forma secuencial")
    void codigosSecuenciales() {
        servicio.crear(PreguntaMother.valida().construir());
        Pregunta segunda = servicio.crear(PreguntaMother.valida().construir());

        assertEquals("A-0228", segunda.getCodigo());
    }

    @Test
    @DisplayName("HU-1, CA2: crear rechaza la pregunta invalida, no la persiste e indica los campos")
    void crearPreguntaInvalida() {
        Pregunta invalida = PreguntaMother.valida().justificacion("").construir();

        ValidacionException excepcion = assertThrows(ValidacionException.class,
                () -> servicio.crear(invalida));

        assertTrue(repositorio.listarTodas().isEmpty());
        assertEquals(ValidacionException.MENSAJE_USUARIO, excepcion.getMessage());
        assertFalse(excepcion.getCamposInvalidos().isEmpty());
    }

    @Test
    @DisplayName("HU-2, CA1: el autor cambia el estado de su pregunta a Pendiente de revision")
    void enviarARevision() {
        Pregunta pregunta = servicio.crear(PreguntaMother.valida().autorLogin("autor1").construir());

        Pregunta actualizada = servicio.enviarARevision(pregunta.getId(), "autor1");

        assertEquals(EstadosPregunta.pendienteRevision().getNombre(),
                actualizada.getEstado().getNombre());
        assertTrue(actualizada.getFechaEnvioRevision() != null);
    }

    @Test
    @DisplayName("HU-2, CA2: no se envia a revision una pregunta incompleta")
    void noEnviaPreguntaIncompleta() {
        // Se guarda directamente en el repositorio para simular un borrador incompleto.
        Pregunta incompleta = PreguntaMother.valida()
                .autorLogin("autor1").bibliografia("").construir();
        repositorio.guardar(incompleta);

        PreguntaIncompletaException excepcion = assertThrows(PreguntaIncompletaException.class,
                () -> servicio.enviarARevision(incompleta.getId(), "autor1"));

        assertEquals(PreguntaIncompletaException.MENSAJE_USUARIO, excepcion.getMessage());
        assertEquals(EstadosPregunta.borrador().getNombre(), incompleta.getEstado().getNombre());
    }

    @Test
    @DisplayName("HU-2: un usuario distinto del autor no puede cambiar el estado")
    void enviarARevisionSinSerAutor() {
        Pregunta pregunta = servicio.crear(PreguntaMother.valida().autorLogin("autor1").construir());

        assertThrows(ReglaNegocioException.class,
                () -> servicio.enviarARevision(pregunta.getId(), "autor2"));
    }

    @Test
    @DisplayName("Enviar a revision una pregunta inexistente lanza excepcion de negocio")
    void enviarARevisionInexistente() {
        assertThrows(ReglaNegocioException.class,
                () -> servicio.enviarARevision("id-que-no-existe", "autor1"));
    }

    @Test
    @DisplayName("HU-3, CA4: el autor edita una pregunta en Borrador")
    void actualizarBorrador() {
        Pregunta pregunta = servicio.crear(PreguntaMother.valida().autorLogin("autor1").construir());
        Pregunta editada = pregunta.aBuilder().tema("Probabilidad").construir();

        servicio.actualizar(editada, "autor1");

        assertEquals("Probabilidad", repositorio.buscarPorId(pregunta.getId()).get().getTema());
    }

    @Test
    @DisplayName("HU-3, CA4: no se edita una pregunta que ya salio de Borrador")
    void noActualizaPreguntaNoEditable() {
        Pregunta pregunta = servicio.crear(PreguntaMother.valida().autorLogin("autor1").construir());
        servicio.enviarARevision(pregunta.getId(), "autor1");
        Pregunta editada = pregunta.aBuilder().tema("Probabilidad").construir();

        assertThrows(ReglaNegocioException.class, () -> servicio.actualizar(editada, "autor1"));
    }

    @Test
    @DisplayName("Solo el autor puede editar su pregunta")
    void soloElAutorEdita() {
        Pregunta pregunta = servicio.crear(PreguntaMother.valida().autorLogin("autor1").construir());

        assertThrows(ReglaNegocioException.class,
                () -> servicio.actualizar(pregunta.aBuilder().construir(), "autor2"));
    }

    @Test
    @DisplayName("HU-3, CA1: el listado solo devuelve las preguntas del autor consultado")
    void listarSoloPreguntasDelAutor() {
        servicio.crear(PreguntaMother.valida().autorLogin("autor1").construir());
        servicio.crear(PreguntaMother.valida().autorLogin("autor1").construir());
        servicio.crear(PreguntaMother.valida().autorLogin("autor2").construir());

        PaginaResultado<Pregunta> pagina =
                servicio.listar(new CriterioBusqueda().autor("autor1"), 1, 10);

        assertEquals(2, pagina.getTotalElementos());
    }

    @Test
    @DisplayName("HU-3, CA1: la paginacion de 10 por pagina reparte correctamente los resultados")
    void paginacion() {
        for (int i = 0; i < 12; i++) {
            servicio.crear(PreguntaMother.valida().autorLogin("autor1").construir());
        }

        PaginaResultado<Pregunta> primera =
                servicio.listar(new CriterioBusqueda().autor("autor1"), 1, 10);
        PaginaResultado<Pregunta> segunda =
                servicio.listar(new CriterioBusqueda().autor("autor1"), 2, 10);

        assertEquals(10, primera.getContenido().size());
        assertEquals(2, segunda.getContenido().size());
        assertEquals(2, primera.getTotalPaginas());
        assertTrue(primera.hayPaginaSiguiente());
        assertTrue(segunda.hayPaginaAnterior());
    }

    @Test
    @DisplayName("HU-3, CA2: el filtro por texto busca por codigo de la pregunta")
    void filtroPorCodigo() {
        Pregunta primera = servicio.crear(PreguntaMother.valida().autorLogin("autor1").construir());
        servicio.crear(PreguntaMother.valida().autorLogin("autor1").construir());

        PaginaResultado<Pregunta> pagina = servicio.listar(
                new CriterioBusqueda().autor("autor1").texto(primera.getCodigo()), 1, 10);

        assertEquals(1, pagina.getTotalElementos());
    }

    @Test
    @DisplayName("HU-3, CA2: los filtros por estado, tema, subtema y competencia se combinan con Y")
    void filtrosCombinados() {
        Pregunta enviada = servicio.crear(PreguntaMother.valida().autorLogin("autor1")
                .nivelDificultad(NivelDificultad.ALTA).construir());
        servicio.enviarARevision(enviada.getId(), "autor1");
        servicio.crear(PreguntaMother.valida().autorLogin("autor1")
                .nivelDificultad(NivelDificultad.ALTA).construir());

        PaginaResultado<Pregunta> pagina = servicio.listar(new CriterioBusqueda()
                .autor("autor1")
                .estado(EstadosPregunta.pendienteRevision())
                .tema("Estadistica")
                .subtema("tendencia")
                .competencia("Razonamiento")
                .nivelDificultad(NivelDificultad.ALTA), 1, 10);

        assertEquals(1, pagina.getTotalElementos());
    }

    @Test
    @DisplayName("HU-3, CA3: una busqueda sin coincidencias devuelve una pagina vacia")
    void busquedaSinResultados() {
        servicio.crear(PreguntaMother.valida().autorLogin("autor1").construir());

        PaginaResultado<Pregunta> pagina = servicio.listar(
                new CriterioBusqueda().autor("autor1").tema("Cinematica"), 1, 10);

        assertEquals(0, pagina.getTotalElementos());
        assertTrue(pagina.estaVacia());
        assertEquals(1, pagina.getTotalPaginas());
    }

    @Test
    @DisplayName("El numero de pagina debe ser mayor o igual que uno")
    void paginaInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.listar(new CriterioBusqueda(), 0, 10));
    }
}
