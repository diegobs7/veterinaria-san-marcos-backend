package cl.duoc.veterinaria.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class UsuarioDtos {

    private UsuarioDtos() {
    }

    public record ActualizarUsuarioRequest(
            @NotBlank @Size(max = 100) String nombre,
            @NotBlank @Email @Size(max = 120) String email) {
    }

    public record CambiarRolRequest(@NotBlank String rol) {
    }
}