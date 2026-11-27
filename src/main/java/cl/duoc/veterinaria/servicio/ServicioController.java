package cl.duoc.veterinaria.servicio;

import static cl.duoc.veterinaria.security.Permisos.ADMIN_VETERINARIA;

import cl.duoc.veterinaria.servicio.ServicioDtos.ServicioRequest;
import cl.duoc.veterinaria.servicio.ServicioDtos.ServicioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/servicios")
@Tag(name = "Servicios")
public class ServicioController {

    private final ServicioService servicio;

    public ServicioController(ServicioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Listar los servicios del catálogo, o solo los de una categoría (público)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de servicios"),
            @ApiResponse(responseCode = "404", description = "La categoría indicada no existe")})
    public List<ServicioResponse> listar(
            @Parameter(description = "Slug de la categoría para filtrar, por ejemplo 'vacunacion'")
            @RequestParam(required = false) String categoria) {
        return categoria == null ? servicio.listar() : servicio.listarPorCategoria(categoria);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver el detalle de un servicio (público)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Servicio encontrado"),
            @ApiResponse(responseCode = "404", description = "Servicio inexistente")})
    public ServicioResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @PreAuthorize(ADMIN_VETERINARIA)
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear un servicio (solo administrador). El código lo genera el servidor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Servicio creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido"),
            @ApiResponse(responseCode = "403", description = "Rol insuficiente"),
            @ApiResponse(responseCode = "404", description = "La categoría indicada no existe"),
            @ApiResponse(responseCode = "409", description = "Ya existe un servicio con ese nombre en la categoría")})
    public ServicioResponse crear(@Valid @RequestBody ServicioRequest body) {
        return servicio.crear(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMIN_VETERINARIA)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Modificar un servicio (solo administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Servicio modificado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido"),
            @ApiResponse(responseCode = "403", description = "Rol insuficiente"),
            @ApiResponse(responseCode = "404", description = "Servicio o categoría inexistente"),
            @ApiResponse(responseCode = "409", description = "Ya existe un servicio con ese nombre en la categoría")})
    public ServicioResponse actualizar(@PathVariable Long id, @Valid @RequestBody ServicioRequest body) {
        return servicio.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ADMIN_VETERINARIA)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar un servicio (solo administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Servicio eliminado"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido"),
            @ApiResponse(responseCode = "403", description = "Rol insuficiente"),
            @ApiResponse(responseCode = "404", description = "Servicio inexistente")})
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}