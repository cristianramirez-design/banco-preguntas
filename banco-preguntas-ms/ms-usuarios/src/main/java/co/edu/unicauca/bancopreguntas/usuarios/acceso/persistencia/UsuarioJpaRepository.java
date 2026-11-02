package co.edu.unicauca.bancopreguntas.usuarios.acceso.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, String> {
}
