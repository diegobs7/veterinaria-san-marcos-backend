package cl.duoc.veterinaria.servicio;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;


public final class ServicioDtos {

    private ServicioDtos() {
    }


    public record ServicioRequest(
            @NotBlank(message = "Ingresa el nombre del servicio")
            @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
            String titulo,

            @NotNull(message = "Selecciona una categoría")
            Long categoriaId,

            @NotBlank(message = "Selecciona una especie")
            @Size(max = 30)
            String especie,

            @NotBlank(message = "Ingresa la duración estimada (ej: 30 min, 1 hr)")
            @Size(max = 30, message = "La duración no puede superar los 30 caracteres")
            String duracion,

            @NotNull(message = "Ingresa el precio del servicio")
            @Min(value = 0, message = "El precio debe ser un número igual o mayor a 0")
            Integer precio,

            @Min(value = 0, message = "El descuento debe estar entre 0 y 100")
            @Max(value = 100, message = "El descuento debe estar entre 0 y 100")
            Integer descuento,

            @Size(max = 150, message = "Las observaciones no pueden superar los 150 caracteres")
            String observaciones,

            @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
            String descripcion,

            @Size(max = 10, message = "La lista de lo que incluye no puede tener más de 10 puntos")
            List<@NotBlank @Size(max = 200) String> incluye) {
    }

    // Se envía el nombre de la categoría además de su id, porque la página lo muestra tal cual
    public record ServicioResponse(Long id, String codigo, String titulo,
                                   Long categoriaId, String categoria, String categoriaSlug,
                                   String especie, String duracion, Integer precio, Integer descuento,
                                   String observaciones, String descripcion, List<String> incluye) {

        public static ServicioResponse de(Servicio s) {
            return new ServicioResponse(s.getId(), s.getCodigo(), s.getTitulo(),
                    s.getCategoria().getId(), s.getCategoria().getNombre(), s.getCategoria().getSlug(),
                    s.getEspecie(), s.getDuracion(), s.getPrecio(), s.getDescuento(),
                    s.getObservaciones(), s.getDescripcion(), List.copyOf(s.getIncluye()));
        }
    }
}