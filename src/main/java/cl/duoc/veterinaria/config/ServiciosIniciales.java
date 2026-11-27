package cl.duoc.veterinaria.config;

import cl.duoc.veterinaria.categoria.Categoria;
import cl.duoc.veterinaria.categoria.CategoriaRepository;
import cl.duoc.veterinaria.common.Slugs;
import cl.duoc.veterinaria.config.CatalogoJson.OfertaJson;
import cl.duoc.veterinaria.config.CatalogoJson.ServicioJson;
import cl.duoc.veterinaria.servicio.Servicio;
import cl.duoc.veterinaria.servicio.ServicioRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

// Carga los servicios del catálogo la primera vez que arranca el backend.
// Va después de CategoriasIniciales (orden 3), porque cada servicio necesita su categoría
@Component
@Order(4)
public class ServiciosIniciales implements ApplicationRunner {

    private final ServicioRepository servicios;
    private final CategoriaRepository categorias;
    private final JsonMapper json;

    public ServiciosIniciales(ServicioRepository servicios, CategoriaRepository categorias, JsonMapper json) {
        this.servicios = servicios;
        this.categorias = categorias;
        this.json = json;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (servicios.count() > 0) {
            return;
        }

        ServicioJson[] datos = leer("data/servicios.json", ServicioJson[].class);
        Map<String, Integer> descuentos = Arrays.stream(leer("data/ofertas.json", OfertaJson[].class))
                .collect(Collectors.toMap(OfertaJson::servicioId, OfertaJson::descuento));

        List<Servicio> nuevos = Arrays.stream(datos)
                .map(d -> crear(d, descuentos.get(d.id())))
                .toList();
        servicios.saveAll(nuevos);
    }

    private Servicio crear(ServicioJson d, Integer descuento) {
        // La categoría se busca por slug, igual que la generó CategoriasIniciales
        Categoria categoria = categorias.findBySlug(Slugs.de(d.categoria()))
                .orElseThrow(() -> new IllegalStateException(
                        "El servicio " + d.codigo() + " tiene una categoría que no existe: " + d.categoria()));

        Servicio s = new Servicio(d.codigo(), d.titulo(), categoria, d.especie(), d.duracion(), d.precio());
        s.setDescuento(descuento); // null si el servicio no está en oferta
        s.setObservaciones(d.observaciones());
        s.setDescripcion(d.descripcion());
        s.setIncluye(d.incluye() == null ? List.of() : d.incluye());
        return s;
    }

    private <T> T leer(String ruta, Class<T> tipo) {
        try (InputStream in = new ClassPathResource(ruta).getInputStream()) {
            return json.readValue(in, tipo);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + ruta, e);
        }
    }
}