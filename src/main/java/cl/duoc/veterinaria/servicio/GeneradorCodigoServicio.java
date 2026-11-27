package cl.duoc.veterinaria.servicio;

import cl.duoc.veterinaria.categoria.Categoria;
import java.util.Locale;
import org.springframework.stereotype.Component;

// Genera el código de un servicio nuevo igual que generarCodigo() del frontend:
// usa el prefijo de su categoría (SV, VA...) y el número siguiente al mayor usado (VA003 -> VA004)
@Component
public class GeneradorCodigoServicio {

    private final ServicioRepository servicios;

    public GeneradorCodigoServicio(ServicioRepository servicios) {
        this.servicios = servicios;
    }

    public String siguiente(Categoria categoria) {
        String prefijo = servicios.findFirstByCategoriaIdOrderByIdAsc(categoria.getId())
                .map(s -> s.getCodigo().substring(0, 2))
                .orElseGet(() -> prefijoDesdeNombre(categoria));

        int mayor = servicios.findByCodigoStartingWith(prefijo).stream()
                .map(s -> s.getCodigo().substring(2))
                .filter(numero -> numero.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        return prefijo + String.format("%03d", mayor + 1);
    }

    // Categoría sin servicios: las dos primeras letras de su nombre ("Dermatología" -> "DE")
    private String prefijoDesdeNombre(Categoria categoria) {
        String letras = categoria.getSlug().replaceAll("[^a-z]", "").toUpperCase(Locale.ROOT);
        return (letras + "XX").substring(0, 2);
    }
}