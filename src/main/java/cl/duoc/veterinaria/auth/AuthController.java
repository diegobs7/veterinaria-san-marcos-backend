package cl.duoc.veterinaria.auth;

import cl.duoc.veterinaria.auth.AuthDtos.*;
import cl.duoc.veterinaria.security.UsuarioPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un usuario (el servidor asigna el rol DUENO)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El correo ya está registrado")})
    public UsuarioResponse registrar(@Valid @RequestBody RegistroRequest body) {
        return auth.registrar(body);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener un JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token emitido"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")})
    public TokenResponse login(@Valid @RequestBody LoginRequest body) {
        return auth.login(body);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Datos del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil del usuario"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido")})
    public UsuarioResponse me(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return auth.perfil(principal.id());
    }
}