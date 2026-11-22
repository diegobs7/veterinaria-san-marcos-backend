package cl.duoc.veterinaria.usuario;

import cl.duoc.veterinaria.auth.AuthDtos.UsuarioResponse;
import cl.duoc.veterinaria.common.ApiException;
import cl.duoc.veterinaria.rol.Rol;
import cl.duoc.veterinaria.rol.RolRepository;
import cl.duoc.veterinaria.security.UsuarioPrincipal;
import cl.duoc.veterinaria.usuario.UsuarioDtos.ActualizarUsuarioRequest;
import cl.duoc.veterinaria.usuario.UsuarioDtos.CambiarRolRequest;
import cl.duoc.veterinaria.usuario.UsuarioDtos.CrearUsuarioRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private static final String ADMIN = "ADMINISTRADOR";

    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarios, RolRepository roles, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarios.findAll().stream().map(UsuarioResponse::de).toList();
    }

    // El administrador crea una cuenta y elige su rol (el registro público siempre da DUENO)
    @Transactional
    public UsuarioResponse crear(CrearUsuarioRequest r) {
        String email = r.email().trim().toLowerCase();
        if (usuarios.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        Rol rol = roles.findByNombre(r.rol().trim().toUpperCase())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "El rol indicado no existe"));
        Usuario u = new Usuario(r.nombre().trim(), email, encoder.encode(r.password()), rol);
        u.setRut(limpiar(r.rut()));
        u.setApellidos(limpiar(r.apellidos()));
        u.setTelefono(limpiar(r.telefono()));
        u.setFechaNacimiento(r.fechaNacimiento());
        u.setRegion(limpiar(r.region()));
        u.setDireccion(limpiar(r.direccion()));
        return UsuarioResponse.de(usuarios.save(u));
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
        u.setRut(limpiar(r.rut()));
        u.setApellidos(limpiar(r.apellidos()));
        u.setTelefono(limpiar(r.telefono()));
        u.setFechaNacimiento(r.fechaNacimiento());
        u.setRegion(limpiar(r.region()));
        u.setDireccion(limpiar(r.direccion()));
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

    // Quita espacios y guarda null en vez de texto vacío (así un campo sin dato queda vacío en la tabla)
    private String limpiar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    // Regla anti-IDOR: cambiar el id de la URL no da acceso a la cuenta de otra persona
    private void exigirPropietarioOAdmin(Long id, UsuarioPrincipal quien) {
        if (!ADMIN.equals(quien.rol()) && !quien.id().equals(id)) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para acceder a la cuenta de otra persona");
        }
    }
}