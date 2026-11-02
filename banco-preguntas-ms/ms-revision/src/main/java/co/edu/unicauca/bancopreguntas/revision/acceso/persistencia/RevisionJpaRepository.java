package co.edu.unicauca.bancopreguntas.revision.acceso.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RevisionJpaRepository extends JpaRepository<RevisionJpaEntity, String> {

    List<RevisionJpaEntity> findByRevisorLogin(String revisorLogin);

    List<RevisionJpaEntity> findByPreguntaId(String preguntaId);

    List<RevisionJpaEntity> findByPreguntaIdAndRonda(String preguntaId, int ronda);
}
