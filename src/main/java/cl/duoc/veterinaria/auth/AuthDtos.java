package cl.duoc.veterinaria.auth;

import cl.duoc.veterinaria.usuario.Aplicacion;
import cl.duoc.veterinaria.usuario.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegistroRequest(
            @NotBlank @Size(max = 100) String nombre,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres") String password,
            @NotNull Aplicacion aplicacion,
            @Size(max = 12) String rut,
            @Size(max = 15) String telefono,
            @Size(max = 300) String direccion) {
    }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password,
                               @NotNull Aplicacion aplicacion) {
    }

    public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
    }

    public record UsuarioResponse(Long id, String nombre, String email, String rol, String aplicacion,
                                  boolean activo, String rut, String apellidos, String telefono,
                                  LocalDate fechaNacimiento, String region, String direccion) {
        public static UsuarioResponse de(Usuario u) {
            return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(),
                    u.getRol().getNombre(), u.getAplicacion().name(), u.isActivo(),
                    u.getRut(), u.getApellidos(), u.getTelefono(),
                    u.getFechaNacimiento(), u.getRegion(), u.getDireccion());
        }
    }
}