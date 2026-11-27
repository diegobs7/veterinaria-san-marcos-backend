package cl.duoc.veterinaria.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public final class CategoriaDtos {

    private CategoriaDtos() {
    }

    public record CategoriaRequest(
            @NotBlank(message = "Ingresa el nombre de la categoría")
            @Size(min = 2, max = 40, message = "El nombre debe tener entre 2 y 40 caracteres")
            String nombre,

            @Size(max = 4, message = "El icono debe ser un solo emoji o símbolo corto")
            String icono,

            @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
            String descripcion) {
    }

    public record CategoriaResponse(Long id, String nombre, String slug, String icono, String descripcion) {

        public static CategoriaResponse de(Categoria c) {
            return new CategoriaResponse(c.getId(), c.getNombre(), c.getSlug(), c.getIcono(), c.getDescripcion());
        }
    }
}