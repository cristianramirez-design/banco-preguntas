package co.edu.unicauca.bancopreguntas.preguntas.presentacion;

import co.edu.unicauca.bancopreguntas.preguntas.domain.excepcion.PreguntaIncompletaException;
import co.edu.unicauca.bancopreguntas.preguntas.domain.excepcion.PreguntaNoEncontradaException;
import co.edu.unicauca.bancopreguntas.preguntas.domain.excepcion.ReglaNegocioException;
import co.edu.unicauca.bancopreguntas.preguntas.domain.excepcion.ValidacionException;
import co.edu.unicauca.bancopreguntas.preguntas.domain.validacion.CampoPregunta;
import co.edu.unicauca.bancopreguntas.preguntas.presentacion.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collection;
import java.util.List;

/** Traduce las excepciones del dominio a respuestas HTTP con un cuerpo uniforme. */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<ErrorResponse> validacion(ValidacionException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage(), e.getErrores(), nombres(e.getCamposInvalidos())));
    }

    @ExceptionHandler(PreguntaIncompletaException.class)
    public ResponseEntity<ErrorResponse> incompleta(PreguntaIncompletaException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage(), e.getErrores(), nombres(e.getCamposInvalidos())));
    }

    @ExceptionHandler(PreguntaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> noEncontrada(PreguntaNoEncontradaException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.simple(e.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> reglaNegocio(ReglaNegocioException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.simple(e.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, MissingRequestHeaderException.class})
    public ResponseEntity<ErrorResponse> peticionInvalida(Exception e) {
        return ResponseEntity.badRequest().body(ErrorResponse.simple(e.getMessage()));
    }

    private static List<String> nombres(Collection<CampoPregunta> campos) {
        return campos.stream().map(Enum::name).toList();
    }
}
