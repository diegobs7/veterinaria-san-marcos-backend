package cl.duoc.veterinaria.usuario;

import cl.duoc.veterinaria.auth.AuthDtos.UsuarioResponse;
import cl.duoc.veterinaria.common.ApiException;
import cl.duoc.veterinaria.rol.Rol;
import cl.duoc.veterinaria.rol.RolRepository;
import cl.duoc.veterinaria.security.UsuarioPrincipal;
import cl.duoc.veterinaria.usuario.UsuarioDtos.ActualizarUsuarioRequest;
import cl.duoc.veterinaria.usuario.UsuarioDtos.CambiarRolRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private static final String ADMIN = "ADMINISTRADOR";

    private final UsuarioRepository usuarios;
    private final RolRepository roles;

    public UsuarioService(UsuarioRepository usuarios, RolRepository roles) {
        this.usuarios = usuarios;
        this.roles = roles;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarios.findAll().stream().map(UsuarioResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id, UsuarioPrincipal quien) {
        exigirPropietarioOAdmin(id, quien);
        return UsuarioResponse.de(buscar(id));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest r, UsuarioPrincipal quien) {
        exigirPropietarioOAdmin(id, quien);
        Usuario u = buscar(id);
        String email = r.email().trim().toLowerCase();
        if (!email.equals(u.getEmail()) && usuarios.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        u.setNombre(r.nombre().trim());
        u.setEmail(email);
        return UsuarioResponse.de(u);
    }

    @Transactional
    public UsuarioResponse cambiarRol(Long id, CambiarRolRequest r, UsuarioPrincipal quien) {
        if (quien.id().equals(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "No puedes cambiar tu propio rol");
        }
        Usuario u = buscar(id);
        Rol rol = roles.findByNombre(r.rol().trim().toUpperCase())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "El rol indicado no existe"));
        u.setRol(rol);
        return UsuarioResponse.de(u);
    }

    @Transactional
    public void desactivar(Long id, UsuarioPrincipal quien) {
        if (quien.id().equals(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "No puedes desactivar tu propia cuenta");
        }
        buscar(id).setActivo(false);
    }

    private Usuario buscar(Long id) {
        return usuarios.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    // Regla anti-IDOR: cambiar el id de la URL no da acceso a la cuenta de otra persona
    private void exigirPropietarioOAdmin(Long id, UsuarioPrincipal quien) {
        if (!ADMIN.equals(quien.rol()) && !quien.id().equals(id)) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para acceder a la cuenta de otra persona");
        }
    }
}