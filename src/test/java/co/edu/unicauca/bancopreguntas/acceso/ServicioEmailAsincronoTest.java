package co.edu.unicauca.bancopreguntas.acceso;

import co.edu.unicauca.bancopreguntas.acceso.email.ServicioEmailAsincrono;
import co.edu.unicauca.bancopreguntas.domain.notificacion.IServicioEmail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ServicioEmailAsincronoTest {

    @Test
    @DisplayName("El decorador delega el envio al servicio envuelto")
    void delega() throws InterruptedException {
        List<String> enviados = new CopyOnWriteArrayList<>();
        ExecutorService ejecutor = Executors.newSingleThreadExecutor();
        IServicioEmail servicio = new ServicioEmailAsincrono((d, a, c) -> enviados.add(d), ejecutor);

        servicio.enviar("luisa@unicauca.edu.co", "asunto", "cuerpo");
        ejecutor.shutdown();
        ejecutor.awaitTermination(2, TimeUnit.SECONDS);

        assertEquals(List.of("luisa@unicauca.edu.co"), enviados);
    }

    @Test
    @DisplayName("Un fallo del servidor de correo no se propaga a quien asigna los revisores")
    void fallaSinPropagar() throws InterruptedException {
        ExecutorService ejecutor = Executors.newSingleThreadExecutor();
        IServicioEmail servicio = new ServicioEmailAsincrono((d, a, c) -> {
            throw new IllegalStateException("SMTP caido");
        }, ejecutor);

        assertDoesNotThrow(() -> servicio.enviar("x@y.co", "a", "c"));
        ejecutor.shutdown();
        ejecutor.awaitTermination(2, TimeUnit.SECONDS);
    }
}
