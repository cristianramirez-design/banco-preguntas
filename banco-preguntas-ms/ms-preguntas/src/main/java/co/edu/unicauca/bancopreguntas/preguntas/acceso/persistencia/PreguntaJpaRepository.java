package co.edu.unicauca.bancopreguntas.preguntas.acceso.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/** Repositorio Spring Data. Solo lo conoce la capa de acceso a datos. */
public interface PreguntaJpaRepository extends JpaRepository<PreguntaJpaEntity, String> {

    @Query("select max(p.codigo) from PreguntaJpaEntity p")
    String buscarMayorCodigo();
}
