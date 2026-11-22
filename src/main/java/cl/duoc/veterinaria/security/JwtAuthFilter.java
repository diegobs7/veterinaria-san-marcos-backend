package cl.duoc.veterinaria.security;

import cl.duoc.veterinaria.usuario.Usuario;
import cl.duoc.veterinaria.usuario.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarios;

    public JwtAuthFilter(JwtService jwtService, UsuarioRepository usuarios) {
        this.jwtService = jwtService;
        this.usuarios = usuarios;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.validar(header.substring(7));
                Long id = Long.valueOf(claims.getSubject());
                usuarios.findById(id).filter(Usuario::isActivo).ifPresent(this::autenticar);
            } catch (JwtException | IllegalArgumentException e) {
                
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }

    private void autenticar(Usuario u) {
        UsuarioPrincipal principal = new UsuarioPrincipal(u.getId(), u.getEmail(), u.getRol().getNombre(), u.getAplicacion());
        var auth = new UsernamePasswordAuthenticationToken(principal, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + principal.rol())));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}