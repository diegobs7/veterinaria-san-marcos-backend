package cl.duoc.veterinaria.servicio;

import cl.duoc.veterinaria.categoria.Categoria;
import cl.duoc.veterinaria.categoria.CategoriaRepository;
import cl.duoc.veterinaria.common.ApiException;
import cl.duoc.veterinaria.servicio.ServicioDtos.ServicioRequest;
import cl.duoc.veterinaria.servicio.ServicioDtos.ServicioResponse;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioService {

    private final ServicioRepository servicios;
    private final CategoriaRepository categorias;
    private final GeneradorCodigoServicio generadorCodigo;

    public ServicioService(ServicioRepository servicios, CategoriaRepository categorias,
                           GeneradorCodigoServicio generadorCodigo) {
        this.servicios = servicios;
        this.categorias = categorias;
        this.generadorCodigo = generadorCodigo;
    }

    @Transactional(readOnly = true)
    public List<ServicioResponse> listar() {
        return servicios.findAll(Sort.by("id")).stream()
                .map(ServicioResponse::de)
                .toList();
    }

    // Servicios de una categoría, para la pagina /categorias/{slug}
    @Transactional(readOnly = true)
    public List<ServicioResponse> listarPorCategoria(String slug) {
        if (categorias.findBySlug(slug).isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
        }
        return servicios.findByCategoriaSlugOrderByIdAsc(slug).stream()
                .map(ServicioResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServicioResponse obtener(Long id) {
        return ServicioResponse.de(buscar(id));
    }

    @Transactional
    public ServicioResponse crear(ServicioRequest r) {
        Categoria categoria = buscarCategoria(r.categoriaId());
        String titulo = r.titulo().trim();
        if (servicios.existsByTituloIgnoreCaseAndCategoriaId(titulo, categoria.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe un servicio con ese nombre en la categoría");
        }
        Servicio s = new Servicio(generadorCodigo.siguiente(categoria), titulo, categoria,
                r.especie().trim(), r.duracion().trim(), r.precio());
        aplicarDetalles(s, r);
        return ServicioResponse.de(servicios.save(s));
    }

    @Transactional
    public ServicioResponse actualizar(Long id, ServicioRequest r) {
        Servicio s = buscar(id);
        Categoria categoria = buscarCategoria(r.categoriaId());
        String titulo = r.titulo().trim();
        if (servicios.existsByTituloIgnoreCaseAndCategoriaIdAndIdNot(titulo, categoria.getId(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe un servicio con ese nombre en la categoría");
        }
        s.setTitulo(titulo);
        s.setCategoria(categoria);
        s.setEspecie(r.especie().trim());
        s.setDuracion(r.duracion().trim());
        s.setPrecio(r.precio());
        aplicarDetalles(s, r);
        return ServicioResponse.de(s);
    }

    @Transactional
    public void eliminar(Long id) {
        servicios.delete(buscar(id));
    }

    // Campos opcionales, iguales al crear y al editar
    private void aplicarDetalles(Servicio s, ServicioRequest r) {
        s.setDescuento(r.descuento() == null || r.descuento() == 0 ? null : r.descuento());
        s.setObservaciones(limpiar(r.observaciones()));
        s.setDescripcion(limpiar(r.descripcion()));
        s.setIncluye(r.incluye() == null ? List.of()
                : r.incluye().stream().map(String::trim).filter(item -> !item.isEmpty()).toList());
    }

    private Servicio buscar(Long id) {
        return servicios.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));
    }

    private Categoria buscarCategoria(Long id) {
        return categorias.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "La categoría indicada no existe"));
    }

    // Guarda null en vez de texto vacío
    private String limpiar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}