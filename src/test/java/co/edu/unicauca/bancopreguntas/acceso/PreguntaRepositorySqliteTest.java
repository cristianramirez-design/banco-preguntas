package co.edu.unicauca.bancopreguntas.acceso;

import co.edu.unicauca.bancopreguntas.acceso.memoria.UsuarioRepositoryMemoria;
import co.edu.unicauca.bancopreguntas.acceso.sqlite.ConexionSqlite;
import co.edu.unicauca.bancopreguntas.acceso.sqlite.GeneradorCodigoSqlite;
import co.edu.unicauca.bancopreguntas.acceso.sqlite.PreguntaRepositorySqlite;
import co.edu.unicauca.bancopreguntas.acceso.sqlite.UsuarioRepositorySqlite;
import co.edu.unicauca.bancopreguntas.domain.model.Pregunta;
import co.edu.unicauca.bancopreguntas.domain.model.Rol;
import co.edu.unicauca.bancopreguntas.domain.model.estado.EstadosPregunta;
import co.edu.unicauca.bancopreguntas.util.PreguntaMother;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Prueba de integracion de la capa de acceso a datos con un archivo SQLite temporal. */
class PreguntaRepositorySqliteTest {

    @TempDir
    Path carpeta;

    private ConexionSqlite conexion;
    private PreguntaRepositorySqlite repositorio;

    @BeforeEach
    void abrir() {
        conexion = new ConexionSqlite(carpeta.resolve("prueba.db").toString());
        repositorio = new PreguntaRepositorySqlite(conexion);
    }

    @AfterEach
    void cerrar() {
        if (conexion != null) {
            conexion.close();
        }
    }

    @Test
    @DisplayName("Guarda y recupera la pregunta con sus opciones y la respuesta correcta")
    void guardarYRecuperar() {
        Pregunta original = PreguntaMother.valida().codigo("A-0227").opcionesConCorrecta(PreguntaMother.OPCIONES, 2)
                .construir();
        repositorio.guardar(original);

        Pregunta leida = repositorio.buscarPorId(original.getId()).orElseThrow();
        assertEquals("A-0227", leida.getCodigo());
        assertEquals(original.getEnunciado(), leida.getEnunciado());
        assertEquals(5, leida.getOpciones().size());
        assertEquals(2, leida.getIndiceRespuestaCorrecta());
        assertEquals(original.getNivelDificultad(), leida.getNivelDificultad());
        assertEquals(EstadosPregunta.borrador(), leida.getEstado());
    }

    @Test
    @DisplayName("Actualizar persiste el nuevo estado y los revisores asignados")
    void actualizarEstadoYRevisores() {
        Pregunta pregunta = PreguntaMother.valida().construir();
        repositorio.guardar(pregunta);
        pregunta.enviarARevision();
        pregunta.asignarRevisores(List.of("revisor1", "revisor2"));

        assertTrue(repositorio.actualizar(pregunta));
        Pregunta leida = repositorio.buscarPorId(pregunta.getId()).orElseThrow();
        assertEquals(EstadosPregunta.enRevision(), leida.getEstado());
        assertEquals(List.of("revisor1", "revisor2"), leida.getRevisores());
        assertEquals(5, leida.getOpciones().size(), "las opciones no se duplican");
    }

    @Test
    @DisplayName("Actualizar una pregunta inexistente devuelve false")
    void actualizarInexistente() {
        assertFalse(repositorio.actualizar(PreguntaMother.valida().construir()));
    }

    @Test
    @DisplayName("Los datos sobreviven a cerrar y reabrir la base de datos")
    void persisteEntreEjecuciones() {
        Pregunta pregunta = PreguntaMother.valida().construir();
        repositorio.guardar(pregunta);
        conexion.close();

        conexion = new ConexionSqlite(carpeta.resolve("prueba.db").toString());
        repositorio = new PreguntaRepositorySqlite(conexion);
        assertEquals(1, repositorio.listarTodas().size());
    }

    @Test
    @DisplayName("El generador continua la secuencia desde el mayor codigo guardado")
    void generadorCodigo() {
        GeneradorCodigoSqlite generador = new GeneradorCodigoSqlite(conexion);
        assertEquals("A-0227", generador.siguiente());
        repositorio.guardar(PreguntaMother.valida().codigo("A-0240").construir());
        assertEquals("A-0241", generador.siguiente());
    }

    @Test
    @DisplayName("El repositorio de usuarios se inicializa con los usuarios de prueba y sus roles")
    void usuarios() {
        UsuarioRepositorySqlite usuarios =
                new UsuarioRepositorySqlite(conexion, new UsuarioRepositoryMemoria().listarTodos());
        assertEquals(6, usuarios.listarTodos().size());
        assertTrue(usuarios.buscarPorLogin("AUTOR2").orElseThrow().tieneRol(Rol.REVISOR));
        assertEquals(4, usuarios.listarPorRol(Rol.REVISOR).size());
    }
}
