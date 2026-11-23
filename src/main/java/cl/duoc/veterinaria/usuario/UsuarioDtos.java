package cl.duoc.veterinaria.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class UsuarioDtos {

    private static final String RUT = "^\\d{1,2}\\.?\\d{3}\\.?\\d{3}-[\\dkK]$";
    private static final String TELEFONO = "^\\+569\\d{8}$";

    private UsuarioDtos() {
    }

    public record ActualizarUsuarioRequest(
            @NotBlank @Size(min = 3, max = 100) String nombre,
            @NotBlank @Email @Size(max = 120) String email,
            @Pattern(regexp = RUT, message = "El RUT debe tener el formato 12.345.678-5") String rut,
            @Size(min = 2, max = 100) String apellidos,
            @Pattern(regexp = TELEFONO, message = "El teléfono debe tener el formato +569xxxxxxxx") String telefono,
            @PastOrPresent LocalDate fechaNacimiento,
            @Size(max = 60) String region,
            @Size(max = 300) String direccion) {
    }

    public record CambiarRolRequest(@NotBlank String rol) {
    }

    public record CrearUsuarioRequest(
            @NotBlank @Size(min = 3, max = 100) String nombre,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres") String password,
            @NotBlank String rol,
            @Pattern(regexp = RUT, message = "El RUT debe tener el formato 12.345.678-5") String rut,
            @Size(min = 2, max = 100) String apellidos,
            @Pattern(regexp = TELEFONO, message = "El teléfono debe tener el formato +569xxxxxxxx") String telefono,
            @PastOrPresent LocalDate fechaNacimiento,
            @Size(max = 60) String region,
            @Size(max = 300) String direccion) {
    }
}