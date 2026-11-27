package cl.duoc.veterinaria.categoria;

import cl.duoc.veterinaria.categoria.CategoriaDtos.CategoriaRequest;
import cl.duoc.veterinaria.categoria.CategoriaDtos.CategoriaResponse;
import cl.duoc.veterinaria.common.ApiException;
import cl.duoc.veterinaria.common.Slugs;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Reglas de negocio de las categorías del catálogo
@Service
public class CategoriaService {

    private final CategoriaRepository categorias;

    public CategoriaService(CategoriaRepository categorias) {
        this.categorias = categorias;
    }

    // Ordenadas por id para respetar el orden en que se cargaron (Consultas, Vacunación...)
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categorias.findAll(Sort.by("id")).stream()
                .map(CategoriaResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Long id) {
        return CategoriaResponse.de(buscar(id));
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtenerPorSlug(String slug) {
        return categorias.findBySlug(slug)
                .map(CategoriaResponse::de)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest r) {
        String slug = slugValido(r.nombre());
        if (categorias.existsBySlug(slug)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
        }
        Categoria c = new Categoria(r.nombre().trim(), slug, limpiar(r.icono()), limpiar(r.descripcion()));
        return CategoriaResponse.de(categorias.save(c));
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest r) {
        Categoria c = buscar(id);
        String slug = slugValido(r.nombre());
        if (categorias.existsBySlugAndIdNot(slug, id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
        }
        c.setNombre(r.nombre().trim());
        c.setSlug(slug);
        c.setIcono(limpiar(r.icono()));
        c.setDescripcion(limpiar(r.descripcion()));
        return CategoriaResponse.de(c);
    }

    @Transactional
    public void eliminar(Long id) {
        categorias.delete(buscar(id));
    }

    private Categoria buscar(Long id) {
        return categorias.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }

    // Un nombre como "!!" pasa la validación de largo, pero no genera slug
    private String slugValido(String nombre) {
        String slug = Slugs.de(nombre);
        if (slug.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El nombre debe tener letras o números");
        }
        return slug;
    }

    // Guarda null en vez de texto vacío
    private String limpiar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}