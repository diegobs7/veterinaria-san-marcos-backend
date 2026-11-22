package cl.duoc.veterinaria.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailAndAplicacion(String email, Aplicacion aplicacion);

    boolean existsByEmailAndAplicacion(String email, Aplicacion aplicacion);

    List<Usuario> findAllByAplicacion(Aplicacion aplicacion);
}