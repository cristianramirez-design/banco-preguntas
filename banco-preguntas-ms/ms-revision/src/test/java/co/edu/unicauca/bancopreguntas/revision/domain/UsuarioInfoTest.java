package co.edu.unicauca.bancopreguntas.revision.domain;

import co.edu.unicauca.bancopreguntas.revision.domain.puerto.UsuarioInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioInfoTest {

    @Test
    @DisplayName("Reconoce los roles de revisor y administrador")
    void roles() {
        UsuarioInfo revisor = new UsuarioInfo("r", "R", "r@x.co", Set.of("REVISOR"));
        UsuarioInfo admin = new UsuarioInfo("a", "A", "a@x.co", Set.of("ADMINISTRADOR"));
        assertTrue(revisor.esRevisor());
        assertFalse(revisor.esAdministrador());
        assertTrue(admin.esAdministrador());
        assertFalse(admin.esRevisor());
    }
}
