package co.edu.unicauca.bancopreguntas.revision.acceso.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaPendienteJpaRepository extends JpaRepository<PreguntaPendienteJpaEntity, String> {

    List<PreguntaPendienteJpaEntity> findByAsignadaFalse();
}
