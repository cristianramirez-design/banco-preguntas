package co.edu.unicauca.bancopreguntas.revision.domain.politica;

import co.edu.unicauca.bancopreguntas.revision.domain.model.EstadoRevision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.ResultadoRevision;
import co.edu.unicauca.bancopreguntas.revision.domain.model.Revision;

import java.util.List;
import java.util.Optional;

/** Se aprueba si la mayoria de revisores aprueba; en empate se rechaza. */
public class PoliticaMayoria implements IPoliticaDecision {

    @Override
    public Optional<ResultadoRevision> decidir(List<Revision> revisiones) {
        if (revisiones.isEmpty() || revisiones.stream().anyMatch(r -> !r.estaEvaluada())) {
            return Optional.empty();
        }
        long aprobadas = revisiones.stream().filter(r -> r.getEstado() == EstadoRevision.APROBADA).count();
        long rechazadas = revisiones.size() - aprobadas;
        return Optional.of(aprobadas > rechazadas ? ResultadoRevision.APROBADA : ResultadoRevision.RECHAZADA);
    }

    @Override
    public String getNombre() {
        return "MAYORIA";
    }
}
