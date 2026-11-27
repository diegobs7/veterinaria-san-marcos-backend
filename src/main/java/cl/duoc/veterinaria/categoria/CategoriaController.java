package cl.duoc.veterinaria.categoria;

import cl.duoc.veterinaria.categoria.CategoriaDtos.CategoriaRequest;
import cl.duoc.veterinaria.categoria.CategoriaDtos.CategoriaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static cl.duoc.veterinaria.security.Permisos.ADMIN_VETERINARIA;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorías")
public class CategoriaController {

    // Solo el administrador de la VETERINARIA: el backend es compartido con Kaiju,
    // y un administrador de Kaiju no debe modificar el catálogo de la veterinaria

    private final CategoriaService servicio;

    public CategoriaController(CategoriaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Listar las categorías del catálogo (público)")
    @ApiResponse(responseCode = "200", description = "Lista de categorías")
    public List<CategoriaResponse> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver una categoría por su id (público)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría inexistente")})
    public CategoriaResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Ver una categoría por su slug, por ejemplo 'vacunacion' (público)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría inexistente")})
    public CategoriaResponse obtenerPorSlug(@PathVariable String slug) {
        return servicio.obtenerPorSlug(slug);
    }

    @PostMapping
    @PreAuthorize(ADMIN_VETERINARIA)
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear una categoría (solo administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoría creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido"),
            @ApiResponse(responseCode = "403", description = "Rol insuficiente"),
            @ApiResponse(responseCode = "409", description = "Ya existe una categoría con ese nombre")})
    public CategoriaResponse crear(@Valid @RequestBody CategoriaRequest body) {
        return servicio.crear(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMIN_VETERINARIA)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Modificar una categoría (solo administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría modificada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido"),
            @ApiResponse(responseCode = "403", description = "Rol insuficiente"),
            @ApiResponse(responseCode = "404", description = "Categoría inexistente"),
            @ApiResponse(responseCode = "409", description = "Ya existe una categoría con ese nombre")})
    public CategoriaResponse actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest body) {
        return servicio.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ADMIN_VETERINARIA)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar una categoría (solo administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoría eliminada"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido"),
            @ApiResponse(responseCode = "403", description = "Rol insuficiente"),
            @ApiResponse(responseCode = "404", description = "Categoría inexistente"),
            @ApiResponse(responseCode = "409", description = "La categoría todavía tiene servicios")})
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}