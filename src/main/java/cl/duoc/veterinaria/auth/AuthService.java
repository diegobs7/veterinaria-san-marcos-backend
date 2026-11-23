package cl.duoc.veterinaria.auth;

import cl.duoc.veterinaria.auth.AuthDtos.*;
import cl.duoc.veterinaria.common.ApiException;
import cl.duoc.veterinaria.rol.Rol;
import cl.duoc.veterinaria.rol.RolRepository;
import cl.duoc.veterinaria.security.JwtService;
import cl.duoc.veterinaria.usuario.Aplicacion;
import cl.duoc.veterinaria.usuario.Usuario;
import cl.duoc.veterinaria.usuario.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final String hashFicticio;

    public AuthService(UsuarioRepository usuarios, RolRepository roles,
                       PasswordEncoder encoder, JwtService jwt) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.encoder = encoder;
        this.jwt = jwt;
        this.hashFicticio = encoder.encode("contrasena-ficticia");
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest r) {
        String email = r.email().trim().toLowerCase();
        Aplicacion app = r.aplicacion();
        if (usuarios.existsByEmailAndAplicacion(email, app)) {
            throw new ApiException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        // El RUT, igual que el correo, no se puede repetir dentro de la misma aplicación
        String rut = limpiar(r.rut());
        if (rut != null && usuarios.existsByRutAndAplicacion(rut, app)) {
            throw new ApiException(HttpStatus.CONFLICT, "El RUT ya está registrado");
        }

        Rol rol = roles.findByNombre(app.rolInicial())
                .orElseThrow(() -> new IllegalStateException("Falta el rol " + app.rolInicial() + " en la base de datos"));
        Usuario u = new Usuario(r.nombre().trim(), email, encoder.encode(r.password()), rol, app);
        u.setRut(rut);
        u.setTelefono(limpiar(r.telefono()));
        u.setDireccion(limpiar(r.direccion()));
        return UsuarioResponse.de(usuarios.save(u));
    }

    public TokenResponse login(LoginRequest r) {
        Usuario u = usuarios.findByEmailAndAplicacion(r.email().trim().toLowerCase(), r.aplicacion())
                .filter(Usuario::isActivo)
                .orElse(null);
        
        String hash = (u != null) ? u.getPasswordHash() : hashFicticio;
        boolean coincide = encoder.matches(r.password(), hash);
        if (u == null || !coincide) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }
        return new TokenResponse(jwt.generar(u), "Bearer", jwt.segundosDeVigencia());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse perfil(Long id) {
        return usuarios.findById(id)
                .map(UsuarioResponse::de)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
    
    // Quita espacios y guarda null en vez de texto vacío
    private String limpiar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}