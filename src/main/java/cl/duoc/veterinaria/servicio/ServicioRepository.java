package cl.duoc.veterinaria.servicio;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    List<Servicio> findByCategoriaSlugOrderByIdAsc(String slug);

    Optional<Servicio> findFirstByCategoriaIdOrderByIdAsc(Long categoriaId);

    List<Servicio> findByCodigoStartingWith(String prefijo);

    boolean existsByTituloIgnoreCaseAndCategoriaId(String titulo, Long categoriaId);

    boolean existsByTituloIgnoreCaseAndCategoriaIdAndIdNot(String titulo, Long categoriaId, Long id);

    boolean existsByCategoriaId(Long categoriaId);
}