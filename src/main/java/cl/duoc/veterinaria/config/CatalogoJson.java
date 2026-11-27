package cl.duoc.veterinaria.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

// Forma de los archivos de resources/data, copiados del frontend
public final class CatalogoJson {

    private CatalogoJson() {
    }

    // Un servicio de servicios.json. La categoría viene por nombre, por ejemplo "Vacunación"
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ServicioJson(
            String id,
            String codigo,
            String categoria,
            String titulo,
            String especie,
            String duracion,
            Integer precio,
            String observaciones,
            String descripcion,
            List<String> incluye) {
    }

    // Una oferta de ofertas.json: el id del servicio y su porcentaje de descuento
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OfertaJson(String servicioId, Integer descuento) {
    }
}