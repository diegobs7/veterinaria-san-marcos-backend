package cl.duoc.veterinaria.config;

import cl.duoc.veterinaria.rol.Rol;
import cl.duoc.veterinaria.rol.RolRepository;
import cl.duoc.veterinaria.usuario.Aplicacion;
import cl.duoc.veterinaria.usuario.Usuario;
import cl.duoc.veterinaria.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Order(2)
@Component
public class AdminInicial implements ApplicationRunner {

    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PasswordEncoder encoder;
    private final String email;
    private final String password;

    public AdminInicial(UsuarioRepository usuarios, RolRepository roles, PasswordEncoder encoder,
                        @Value("${app.admin.email:}") String email,
                        @Value("${app.admin.password:}") String password) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.encoder = encoder;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() || password.isBlank()) {
            return;
        }
        Rol admin = roles.findByNombre("ADMINISTRADOR")
                .orElseThrow(() -> new IllegalStateException("Falta el rol ADMINISTRADOR"));
        
        for (Aplicacion app : Aplicacion.values()) {
            if (!usuarios.existsByEmailAndAplicacion(email.toLowerCase(), app)) {
                usuarios.save(new Usuario("Administrador", email.toLowerCase(),
                        encoder.encode(password), admin, app));
            }
        }
    }
}