package co.edu.unicauca.bancopreguntas.revision.domain.politica;

import co.edu.unicauca.bancopreguntas.revision.domain.model.EstadoRevision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.ResultadoRevision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Revision;

import java.util.List;
import java.util.Optional;

/** Se aprueba solo si TODOS los revisores aprueban. Basta un rechazo para rechazar. */
public class PoliticaUnanimidad implements IPoliticaDecision {

    @Override
    public Optional<ResultadoRevision> decidir(List<Revision> revisiones) {
        if (revisiones.isEmpty() || revisiones.stream().anyMatch(r -> !r.estaEvaluada())) {
            return Optional.empty();
        }
        boolean algunRechazo = revisiones.stream().anyMatch(r -> r.getEstado() == EstadoRevision.RECHAZADA);
        return Optional.of(algunRechazo ? ResultadoRevision.RECHAZADA : ResultadoRevision.APROBADA);
    }

    @Override
    public String getNombre() {
        return "UNANIMIDAD";
    }
}
