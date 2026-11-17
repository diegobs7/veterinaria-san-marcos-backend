package cl.duoc.veterinaria.auth;

import cl.duoc.veterinaria.auth.AuthDtos.*;
import cl.duoc.veterinaria.common.ApiException;
import cl.duoc.veterinaria.rol.Rol;
import cl.duoc.veterinaria.rol.RolRepository;
import cl.duoc.veterinaria.security.JwtService;
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
        if (usuarios.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        // El rol lo asigna el servidor: el cliente nunca puede elegirlo
        Rol rol = roles.findByNombre("DUENO")
                .orElseThrow(() -> new IllegalStateException("Falta el rol DUENO en la base de datos"));
        Usuario u = usuarios.save(new Usuario(r.nombre().trim(), email, encoder.encode(r.password()), rol));
        return UsuarioResponse.de(u);
    }

    public TokenResponse login(LoginRequest r) {
        Usuario u = usuarios.findByEmail(r.email().trim().toLowerCase())
                .filter(Usuario::isActivo)
                .orElse(null);
        // Se compara siempre contra un hash, exista o no la cuenta, para no revelar cuál falló
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
}