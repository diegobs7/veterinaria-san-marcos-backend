package cl.duoc.veterinaria.usuario;

import cl.duoc.veterinaria.auth.AuthDtos.UsuarioResponse;
import cl.duoc.veterinaria.security.UsuarioPrincipal;
import cl.duoc.veterinaria.usuario.UsuarioDtos.ActualizarUsuarioRequest;
import cl.duoc.veterinaria.usuario.UsuarioDtos.CambiarRolRequest;
import cl.duoc.veterinaria.usuario.UsuarioDtos.CrearUsuarioRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar los usuarios de tu aplicación (solo administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido"),
            @ApiResponse(responseCode = "403", description = "Rol insuficiente")})
    public List<UsuarioResponse> listar(@AuthenticationPrincipal UsuarioPrincipal quien) {
        return servicio.listar(quien);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un usuario con su rol (solo administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o el rol no existe"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido"),
            @ApiResponse(responseCode = "403", description = "Rol insuficiente"),
            @ApiResponse(responseCode = "409", description = "El correo ya está registrado")})
    public UsuarioResponse crear(@Valid @RequestBody CrearUsuarioRequest body,
                                 @AuthenticationPrincipal UsuarioPrincipal quien) {
        return servicio.crear(body, quien);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver un usuario (el propio o cualquiera si eres administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "403", description = "Cuenta de otra persona"),
            @ApiResponse(responseCode = "404", description = "Usuario inexistente")})
    public UsuarioResponse obtener(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal quien) {
        return servicio.obtener(id, quien);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar nombre y correo (el propio o cualquiera si eres administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario modificado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Cuenta de otra persona"),
            @ApiResponse(responseCode = "404", description = "Usuario inexistente"),
            @ApiResponse(responseCode = "409", description = "El correo ya está registrado")})
    public UsuarioResponse actualizar(@PathVariable Long id,
                                      @Valid @RequestBody ActualizarUsuarioRequest body,
                                      @AuthenticationPrincipal UsuarioPrincipal quien) {
        return servicio.actualizar(id, body, quien);
    }

    @PatchMapping("/{id}/rol")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cambiar el rol de un usuario (solo administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol cambiado"),
            @ApiResponse(responseCode = "400", description = "El rol no existe"),
            @ApiResponse(responseCode = "403", description = "Rol insuficiente"),
            @ApiResponse(responseCode = "404", description = "Usuario inexistente"),
            @ApiResponse(responseCode = "409", description = "No puedes cambiar tu propio rol")})
    public UsuarioResponse cambiarRol(@PathVariable Long id,
                                      @Valid @RequestBody CambiarRolRequest body,
                                      @AuthenticationPrincipal UsuarioPrincipal quien) {
        return servicio.cambiarRol(id, body, quien);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desactivar un usuario (solo administrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario desactivado"),
            @ApiResponse(responseCode = "403", description = "Rol insuficiente"),
            @ApiResponse(responseCode = "404", description = "Usuario inexistente"),
            @ApiResponse(responseCode = "409", description = "No puedes desactivar tu propia cuenta")})
    public void desactivar(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal quien) {
        servicio.desactivar(id, quien);
    }
}